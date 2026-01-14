package com.andresen.macrocalculatorbackend;

import java.sql.Timestamp;
import java.util.Objects;

public class UserProfile {
    private Integer id;
    private Integer userId;
    private String name;
    private double weightGrams;
    private double heightCm;
    private String activityLevel;
    private double bodyFatPercentage;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserProfile(Integer id, Integer userId, String name, double weightGrams, double heightCm, String activityLevel, double bodyFatPercentage, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.weightGrams = weightGrams;
        this.heightCm = heightCm;
        this.activityLevel = activityLevel;
        this.bodyFatPercentage = bodyFatPercentage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeightGrams() {
        return weightGrams;
    }

    public void setWeightGrams(double weightGrams) {
        this.weightGrams = weightGrams;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    public double getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(double bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Double.compare(weightGrams, that.weightGrams) == 0 && Double.compare(heightCm, that.heightCm) == 0 && Double.compare(bodyFatPercentage, that.bodyFatPercentage) == 0 && Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(name, that.name) && Objects.equals(activityLevel, that.activityLevel) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, name, weightGrams, heightCm, activityLevel, bodyFatPercentage, createdAt, updatedAt);
    }
}
