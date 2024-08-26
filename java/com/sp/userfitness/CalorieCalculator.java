package com.sp.userfitness;

public class CalorieCalculator {

    public static int calculateCaloriesBurned(long elapsedTimeInSeconds, double weightInKg, double MET) {

        double calories = (MET * 3.5 * weightInKg / 200) * (elapsedTimeInSeconds / 60.0);

        return (int) Math.round(calories);
    }
}
