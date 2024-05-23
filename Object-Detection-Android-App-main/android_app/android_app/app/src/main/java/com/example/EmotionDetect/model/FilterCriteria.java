package com.example.EmotionDetect.model;

public class FilterCriteria {
    private float happinessThreshold;
    private float sadnessThreshold;
    private float fearThreshold;
    private float angerThreshold;
    private float disgustThreshold;
    private float surpriseThreshold;
    private String sortCriteria;

    public FilterCriteria(float happinessThreshold, float sadnessThreshold, float fearThreshold, float angerThreshold, float disgustThreshold, float surpriseThreshold, String sortCriteria) {
        this.happinessThreshold = happinessThreshold;
        this.sadnessThreshold = sadnessThreshold;
        this.fearThreshold = fearThreshold;
        this.angerThreshold = angerThreshold;
        this.disgustThreshold = disgustThreshold;
        this.surpriseThreshold = surpriseThreshold;
        this.sortCriteria = sortCriteria;
    }

    public float getHappinessThreshold() {
        return happinessThreshold;
    }

    public float getSadnessThreshold() {
        return sadnessThreshold;
    }

    public float getFearThreshold() {
        return fearThreshold;
    }

    public float getAngerThreshold() {
        return angerThreshold;
    }

    public float getDisgustThreshold() {
        return disgustThreshold;
    }

    public float getSurpriseThreshold() {
        return surpriseThreshold;
    }

    public String getSortCriteria() {
        return sortCriteria;
    }
}
