package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalTime;
import java.util.List;

public class MealService implements Service {

    private MealRepository repository;

    public MealService() {
        this.repository = new MealRepository();
    }

    @Override
    public <T> void add(T t) {
        repository.add((Meal) t);
    }

    public void delete(int Id) {
        repository.deleteById(Id);
    }

    @Override
    public <T> void update(int Id, T t) throws Exception {
        if (repository.existById(Id)) {
            repository.update(Id, (Meal) t);
        } else {
            throw new Exception();
        }
    }

    public List<MealTo> getAll() {
        return MealsUtil.filteredByStreams(repository.getAll(), LocalTime.MIN, LocalTime.MAX, 2000);
    }

    public Meal getById(int Id) {
        return repository.getById(Id);
    }
}
