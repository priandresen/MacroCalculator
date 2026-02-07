package com.andresen.macrocalculatorbackend.logs;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/user-profile/{userId}/daily-logs")
public class UserDailyLogController {

    private final UserDailyLogService service;

    public UserDailyLogController(UserDailyLogService service) {
        this.service = service;
    }

    @GetMapping("/{date}")
    public DailyLogDTO getDailyLog(
            @PathVariable Long userId,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
            @PathVariable LocalDate date
    ) {
        return service.getDailyLog(userId, date);
    }

    @GetMapping("/{dailyLogId}/foods/{foodLogId}")
    public FoodLogDTO getFoodLog(
            @PathVariable Long dailyLogId,
            @PathVariable Long foodLogId
    ){
        return service.getFoodLog(dailyLogId, foodLogId);
    }

    @DeleteMapping("/{dailyLogId}/foods/{foodLogId}")
    public void deleteFoodLog(
            @PathVariable Long dailyLogId,
            @PathVariable Long foodLogId
    ) {
        service.deleteFoodLog(dailyLogId, foodLogId);
    }

//    @PostMapping("/{date}")
//    public DailyLogDTO addFoodToDay(
//            @PathVariable Long userId,
////            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
//            @PathVariable LocalDate date,
//            @RequestBody AddFoodRequest request
//    ) {
//        return service.addFoodToDay(
//                userId,
//                date,
//                request.fdcId(),
//                request.servingSize()
//        );
//    }

    @PostMapping("/{date}")
    public ResponseEntity<Void> addFoodToDay(
            @PathVariable Long userId,
            @PathVariable LocalDate date,
            @RequestBody AddFoodRequest request
    ) {
        service.addFoodToDay(userId, date, request.fdcId(), request.servingSize());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{dailyLogId}/foods/{foodLogId}")
    public FoodLogDTO patchFoodLog(
            @PathVariable Long dailyLogId,
            @PathVariable Long foodLogId,
            @RequestBody @Valid UpdateFoodLogDTO request
    ) {
        return service.patchFoodLog(dailyLogId, foodLogId, request);
    }




}
