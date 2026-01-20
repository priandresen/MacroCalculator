package com.andresen.macrocalculatorbackend.macrogoal;

import com.andresen.macrocalculatorbackend.userprofile.ActivityLevel;
import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name="macro_goal")
public class MacroGoal {

    protected MacroGoal() {}
    // why does it have to be protected?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_profile", referencedColumnName = "id")
    private UserProfile userProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private ActivityLevel.GoalType goalType;

    @Column(name = "height_cm", nullable = false)
    private Double heightCm;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", nullable = false)
    private ActivityLevel activityLevel;

    @Column(name = "body_fat_percentage")
    private Double bodyFatPercentage;

    @Column(
            name = "created_at",
            insertable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            insertable = false,
            updatable = false
    )
    private Instant updatedAt;

    public UserProfile(String name, Double weightGrams, Double heightCm, ActivityLevel activityLevel, Double bodyFatPercentage) {
        this.name = name;
        this.weightGrams = weightGrams;
        this.heightCm = heightCm;
        this.activityLevel = activityLevel;
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public Long getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(Double weightGrams) {
        this.weightGrams = weightGrams;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public Double getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(Double bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }


    public Instant getUpdatedAt() {
        return updatedAt;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        com.andresen.macrocalculatorbackend.userprofile.UserProfile that = (com.andresen.macrocalculatorbackend.userprofile.UserProfile) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

