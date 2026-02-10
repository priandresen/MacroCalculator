package com.andresen.macrocalculatorbackend.logs;

import com.andresen.macrocalculatorbackend.exception.ResourceNotFoundException;
import com.andresen.macrocalculatorbackend.foodAPI.FoodDetailsDTO;
import com.andresen.macrocalculatorbackend.foodAPI.FoodService;
import com.andresen.macrocalculatorbackend.userprofile.UpdateUserProfileDTO;
import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileDTO;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserDailyLogService {

    private final UserDailyLogRepository repo;
    private final UserDailyLogDTOMapper mapper;
    private final FoodService foodService;
    private final UserProfileRepository userRepo;

    public UserDailyLogService(UserDailyLogRepository repo, UserDailyLogDTOMapper mapper, FoodService foodService, UserProfileRepository userRepo) {
        this.repo = repo;
        this.mapper = mapper;
        this.foodService = foodService;
        this.userRepo = userRepo;
    }

    @Transactional
    public void deleteFoodLog(Long dailyLogId, Long foodLogId) {
        UserDailyLog log = repo.findByIdWithFoods(dailyLogId)
                .orElseThrow(() -> new IllegalArgumentException("Daily log not found"));

        boolean removed = log.removeFoodItemById(foodLogId);
        if (!removed) {
            throw new IllegalArgumentException("Food log not found");
        }
    }

    @Transactional
    public FoodLogDTO getFoodLog(Long dailyLogId, Long foodLogId){
        UserDailyLog log = repo.findByIdWithFoods(dailyLogId)
                .orElseThrow(() -> new IllegalArgumentException("Daily log not found"));

        FoodLog foodLog = log.getFoodLogs().stream()
                .filter(f -> f.getId().equals(foodLogId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Food log not found"));

        return mapper.toFoodDTO(foodLog);
    }

    @Transactional
    public DailyLogDTO getDailyLog(Long userId, LocalDate date) {
        UserDailyLog log = getOrCreateLog(userId, date);
        return mapper.toDTO(log);
    }

    private UserDailyLog getOrCreateLog(Long userId, LocalDate date) {
        return repo.findByUserIdAndDateWithFoods(userId, date)
                .orElseGet(() -> {

                    UserProfile user = userRepo.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));

                    try {

                        return repo.save(new UserDailyLog(date, user));

                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        return repo.findByUserIdAndDateWithFoods(userId, date)
                                .orElseThrow(() ->
                                        new IllegalStateException("Daily log exists but cannot be loaded", e)
                                );
                    }
                });
    }


    @Transactional
    public DailyLogDTO addFoodToDay(
            Long userId,
            LocalDate date,
            Long fdcId,
            Double servingSize) {

//        UserDailyLog log = repo
//                .findByUserIdAndDateWithFoods(userId, date)
//                .orElseGet(() -> {
//                    UserProfile user = userRepo.findById(userId)
//                            .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//                    UserDailyLog newLog = new UserDailyLog(date, user);
//                    return repo.save(newLog);
//                });

            UserDailyLog log = getOrCreateLog(userId, date);

        // 2. External API → internal DTO
        FoodDetailsDTO details = foodService.getFoodDetails(fdcId);

        Double factor = servingSize / details.servingSize();

        // 3. DTO → entity
        FoodLog foodLog = new FoodLog(
                details.name(),
                servingSize,
                details.servingUnit(),
                scale(details.calories(), factor),
                scale(details.protein(), factor),
                scale(details.carbs(), factor),
                scale(details.fat(), factor)

        );

        foodLog.setUsdaId(fdcId);
        foodLog.setBrand(details.brand());


        log.addFoodItem(foodLog);

        repo.save(log);

        return mapper.toDTO(log);
    }

    private Integer scale(Double base, Double factor) {
        if (base == null) return 0;
        return (int) Math.round(base * factor);
    }

    @Transactional
    public FoodLogDTO patchFoodLog(Long dailyLogId, Long foodLogId, @Valid UpdateFoodLogDTO request) {
        UserDailyLog log = repo.findByIdWithFoods(dailyLogId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "daily log with id [%s] not found".formatted(dailyLogId)
                ));

        FoodLog foodLog = log.getFoodLogs().stream()
                .filter(f -> f.getId().equals(foodLogId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "food log with id [%s] not found in daily log [%s]".formatted(foodLogId, dailyLogId)
                ));

        if (request.servingSize() != null) {
            if (foodLog.getUsdaId() == null) {
                throw new IllegalArgumentException("Food log has no usdaId; cannot recalculate macros.");
            }

            FoodDetailsDTO details = foodService.getFoodDetails(foodLog.getUsdaId());

            Double factor = request.servingSize() / details.servingSize();

            foodLog.setServingSize(request.servingSize());
            foodLog.setServingUnit(details.servingUnit());
            foodLog.setName(details.name());
            foodLog.setBrand(details.brand());

            foodLog.setCalories(scale(details.calories(), factor));
            foodLog.setProteinG(scale(details.protein(), factor));
            foodLog.setCarbsG(scale(details.carbs(), factor));
            foodLog.setFatG(scale(details.fat(), factor));
        }

        // In a transaction, dirty-checking is enough, but saving is fine for clarity
        repo.save(log);

        return mapper.toFoodDTO(foodLog);
    }


}
