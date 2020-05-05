package ru.javawebinar.topjava.model;

import sun.util.resources.LocaleData;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserMeal {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    public UserMeal(LocalDateTime dateTime, String description, int calories) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDate getLocalDate() {
        return this.getDateTime().toLocalDate();
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }
}
