package com.andresen.macrocalculatorbackend.logs;

import com.andresen.macrocalculatorbackend.foodAPI.FoodDetailsDTO;
import com.andresen.macrocalculatorbackend.foodAPI.FoodService;
import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import com.andresen.macrocalculatorbackend.userprofile.UserProfileRepository;
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

    @Transactional(readOnly = true)
    public DailyLogDTO getDailyLog(Long userId, LocalDate date) {
        UserDailyLog log = repo.findByUserIdAndDateWithFoods(userId, date)
                .orElseThrow(() -> new IllegalArgumentException("Daily log not found"));
        return mapper.toDTO(log);
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
    public DailyLogDTO addFoodToDay(
            Long userId,
            LocalDate date,
            Long fdcId,
            Double servingSize) {

        UserDailyLog log = repo
                .findByUserIdAndDateWithFoods(userId, date)
                .orElseGet(() -> {
                    UserProfile user = userRepo.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("User not found"));

                    UserDailyLog newLog = new UserDailyLog(date, user);
                    return repo.save(newLog);
                });

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

        return mapper.toDTO(log);
    }

    private Integer scale(Double base, Double factor) {
        if (base == null) return 0;
        return (int) Math.round(base * factor);
    }





}
