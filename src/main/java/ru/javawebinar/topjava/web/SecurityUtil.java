package ru.javawebinar.topjava.web;

import static ru.javawebinar.topjava.util.MealsUtil.DEFAULT_CALORIES_PER_DAY;

public class SecurityUtil {

    private int authUserId = 1;

    public int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }

    public int getAuthUserId() {
        return authUserId;
    }

    public void setAuthUserId(int authUserId) {
        authUserId = authUserId;
    }
}