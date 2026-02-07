package com.andresen.macrocalculatorbackend.logs;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name="food_log")
public class FoodLog {

    protected FoodLog() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="usda_id")
    private Long usdaId;

    @ManyToOne
    @NotNull
    @JoinColumn(nullable = false, name="user_daily_log_id")
    private UserDailyLog userDailyLog;

    @Column(name="name", nullable = false)
    @NotNull
    private String name;

    @Column(name="brand")
    private String brand;

    @Column(name="serving_size", nullable = false)
    @NotNull
    private Double servingSize;

    @Column(name="serving_unit", nullable = false)
    @NotNull
    private String servingUnit;

    @Column(name="calories", nullable = false)
    @NotNull
    private Integer calories;

    @Column(name="protein_g", nullable = false)
    @NotNull
    private Integer proteinG;

    @Column(name="carbs_g", nullable = false)
    @NotNull
    private Integer carbsG;

    @Column(name="fat_g", nullable = false)
    @NotNull
    private Integer fatG;

    public FoodLog(String name, Double servingSize, String servingUnit, Integer calories, Integer proteinG, Integer carbsG, Integer fatG) {

        if (name == null) throw new IllegalArgumentException("name is required");
        if (servingSize == null) throw new IllegalArgumentException("servingSize is required");
        if (servingUnit == null) throw new IllegalArgumentException("servingUnit is required");
        if (calories == null) throw new IllegalArgumentException("calories is required");
        if (proteinG == null) throw new IllegalArgumentException("proteinG is required");
        if (carbsG == null) throw new IllegalArgumentException("carbsG is required");
        if (fatG == null) throw new IllegalArgumentException("fatG is required");

        this.name = name;
        this.servingSize = servingSize;
        this.servingUnit = servingUnit;
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
    }

    public @NotNull UserDailyLog getUserDailyLog() {
        return userDailyLog;
    }

    public void setUsdaId(Long usdaId){
        this.usdaId = usdaId;
    }

    public void setBrand(String brand){
        this.brand = brand;
    }

    public Long getId() {
        return id;
    }

    public Long getUsdaId() {
        return usdaId;
    }

    public String getName() {
        return name;
    }

    public String getBrand() { return brand; }

    public Double getServingSize() { return servingSize; }
    public void setServingSize(Double servingSize){
        if (servingSize == null) throw new IllegalArgumentException("servingSize is required");
        if (servingSize <= 0) throw new IllegalArgumentException("servingSize must be > 0");
        this.servingSize = servingSize;
    }
    public void setCalories(Integer calories) {
        if (calories == null) throw new IllegalArgumentException("calories is required");
        this.calories = calories;
    }

    public void setProteinG(Integer proteinG) {
        if (proteinG == null) throw new IllegalArgumentException("proteinG is required");
        this.proteinG = proteinG;
    }

    public void setCarbsG(Integer carbsG) {
        if (carbsG == null) throw new IllegalArgumentException("carbsG is required");
        this.carbsG = carbsG;
    }

    public void setFatG(Integer fatG) {
        if (fatG == null) throw new IllegalArgumentException("fatG is required");
        this.fatG = fatG;
    }

    public String getServingUnit() { return servingUnit; }
    public Integer getCalories() { return calories; }
    public Integer getProteinG() { return proteinG; }
    public Integer getCarbsG() { return carbsG; }
    public Integer getFatG() { return fatG; }

    void setUserDailyLog(UserDailyLog userDailyLog) {
        this.userDailyLog = userDailyLog;
    }

    public void setServingUnit(String s) {
        if (servingUnit == null) throw new IllegalArgumentException("servingUnit is required");
        this.servingUnit = servingUnit;
    }

    public void setName(String name) {
        if (name == null) throw new IllegalArgumentException("name is required");
        this.name = name;
    }
}