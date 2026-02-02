package com.andresen.macrocalculatorbackend.logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDailyLogRepository extends JpaRepository<UserDailyLog, Long> {

    @Query("""
           select l
           from UserDailyLog l
           left join fetch l.foodLogs fl
           where l.userProfile.id = :userId
             and l.logDate = :date
           """)
    Optional<UserDailyLog> findByUserIdAndDateWithFoods(
            @Param("userId") Long userId,
            @Param("date") LocalDate date
    );

    @Query("""
           select l
           from UserDailyLog l
           left join fetch l.foodLogs fl
           where l.id = :logId
           """)
    Optional<UserDailyLog> findByIdWithFoods(@Param("logId") Long logId);
}
