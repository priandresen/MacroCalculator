package com.andresen.macrocalculatorbackend.macrogoal;

import com.andresen.macrocalculatorbackend.userprofile.UserProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name="macro_goal")
public class MacroGoal {

    protected MacroGoal() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id", nullable = false)
    private UserProfile userProfile;

    @Column(name = "calorie_target", nullable = false)
    private Integer calorieTarget;

    @Column(name = "protein_g", nullable = false)
    private Double proteinG;

    @Column(name = "carbs_g", nullable = false)
    private Double carbsG;

    @Column(name = "fat_g", nullable = false)
    private Double fatG;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(
            name = "created_at",
            insertable = false,
            updatable = false
    )
    private Instant createdAt;

    public MacroGoal(UserProfile userProfile, Integer calorieTarget, Double proteinG, Double carbsG, Double fatG, boolean isActive) {
        this.userProfile = userProfile;
        this.calorieTarget = calorieTarget;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public @NotNull UserProfile getUserProfile() {
        return userProfile;
    }

    public Integer getCalorieTarget() {
        return calorieTarget;
    }

    public void setCalorieTarget(Integer calorieTarget) {
        this.calorieTarget = calorieTarget;
    }

    public Double getProteinG() {
        return proteinG;
    }

    public void setProteinG(Double proteinG) {
        this.proteinG = proteinG;
    }

    public Double getCarbsG() {
        return carbsG;
    }

    public void setCarbsG(Double carbsG) {
        this.carbsG = carbsG;
    }

    public Double getFatG() {
        return fatG;
    }

    public void setFatG(Double fatG) {
        this.fatG = fatG;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MacroGoal macroGoal = (MacroGoal) o;
        return Objects.equals(id, macroGoal.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}


//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        com.andresen.macrocalculatorbackend.userprofile.UserProfile that = (com.andresen.macrocalculatorbackend.userprofile.UserProfile) o;
//        return id != null && Objects.equals(id, that.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return getClass().hashCode();
//    }

