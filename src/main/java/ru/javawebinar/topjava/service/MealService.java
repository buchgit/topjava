package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.List;

public class MealService {
    private MealRepository repository;

    public MealService() {
        this.repository = new MealRepository();
    }

    public List<MealTo> getList() {
        return MealsUtil.transformTo(repository.getList(), 2000);
    }
}
