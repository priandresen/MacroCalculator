package com.andresen.macrocalculatorbackend.logs;

import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "user_daily_log")
public class UserDailyLog {

    protected UserDailyLog() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "userDailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<FoodLog> foodLogs = new ArrayList<>();

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "log_date", nullable = false)
    @NotNull
    private LocalDate logDate;

    public UserDailyLog(@NotNull LocalDate logDate, @NotNull UserProfile userProfile) {
        if (logDate == null) throw new IllegalArgumentException("logDate is required");
        if (userProfile == null) throw new IllegalArgumentException("userProfile is required");
        this.logDate = logDate;
        this.userProfile = userProfile;
    }

    public Long getId() {
        return id;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }
    public LocalDate getLogDate() {
        return logDate;
    }

    public List<FoodLog> getFoodLogs() {
        return List.copyOf(foodLogs);
    }

    public void addFoodItem(@NotNull FoodLog item) {
        if (item == null) throw new IllegalArgumentException("FoodLog is required");
        foodLogs.add(item);
        item.setUserDailyLog(this);
    }


    public boolean removeFoodItemById(@NotNull Long foodLogId) {
        if (foodLogId == null) throw new IllegalArgumentException("foodLogId is required");

        Iterator<FoodLog> it = foodLogs.iterator();
        while (it.hasNext()) {
            FoodLog f = it.next();
            if (foodLogId.equals(f.getId())) {
                it.remove();
                f.setUserDailyLog(null); // critical: orphan => deleted on flush/commit
                return true;
            }
        }
        return false;
    }
}
