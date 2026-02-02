package com.andresen.macrocalculatorbackend.logs;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/user-profile/{userId}/daily-logs")
public class UserDailyLogController {

    private final UserDailyLogService service;

    public UserDailyLogController(UserDailyLogService service) {
        this.service = service;
    }

    @GetMapping
    public DailyLogDTO getDailyLog(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.getDailyLog(userId, date);
    }

    @DeleteMapping("/{dailyLogId}/foods/{foodLogId}")
    public void deleteFoodLog(
            @PathVariable Long dailyLogId,
            @PathVariable Long foodLogId
    ) {
        service.deleteFoodLog(dailyLogId, foodLogId);
    }

    @PostMapping("/foods")
    public DailyLogDTO addFoodToDay(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody AddFoodRequest request
    ) {
        return service.addFoodToDay(
                userId,
                date,
                request.fdcId(),
                request.servingSize()
        );
    }


}
