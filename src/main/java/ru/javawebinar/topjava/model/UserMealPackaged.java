package ru.javawebinar.topjava.model;

import java.time.LocalTime;

public class UserMealPackaged {
    private UserMeal userMeal;
    private LocalTime startTime;
    private LocalTime endTime;

    public UserMealPackaged(UserMeal userMeal, LocalTime startTime, LocalTime endTime) {
        this.userMeal = userMeal;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UserMeal getUserMeal() {
        return userMeal;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

}
