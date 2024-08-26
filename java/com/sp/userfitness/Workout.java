package com.sp.userfitness;

public class Workout {
    private final String date;
    private final String activityName;
    private final long duration;
    private final double caloriesBurnt;

    public Workout(String date, String activityName, long duration, double caloriesBurnt) {
        this.date = date;
        this.activityName = activityName;
        this.duration = duration;
        this.caloriesBurnt = caloriesBurnt;
    }

    public String getDate() {
        return date;
    }

    public String getActivityName() {
        return activityName;
    }

    public long getDuration() {
        return duration;
    }

    public double getCaloriesBurnt() {
        return (int) Math.round(caloriesBurnt);
    }

    public String getFormattedDuration() {
        int secs = (int) (duration % 60);
        int mins = (int) ((duration / 60) % 60);
        int hours = (int) (duration / 3600);
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
}