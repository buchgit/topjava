package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MealRestController extends AbstractMealController {

    @Override
    public List<Meal> getAll(int userId, LocalDateTime startDate, LocalDateTime endDate) {
        return super.getAll(userId, startDate, endDate);
    }

    @Override
    public Meal get(int id, int userId) {
        return super.get(id, userId);
    }

    @Override
    public Meal create(Meal meal, int userId) {
        return super.create(meal, userId);
    }

    @Override
    public void delete(int id, int userId) {
        super.delete(id, userId);
    }

    @Override
    public void update(Meal meal, int mealId, int userId) {
        super.update(meal, mealId, userId);
    }
}