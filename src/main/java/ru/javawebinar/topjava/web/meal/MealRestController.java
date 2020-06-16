package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<Meal> getAll() {
        return (List<Meal>) service.getAll(new SecurityUtil().getAuthUserId());
    }

    public List<MealTo> getTos(String startDate, String endDate, String startTime, String endTime) {
        LocalDateTime sd = null;
        LocalDateTime ed = null;
        try {
            sd = LocalDateTime.parse(startDate);
        } catch (Exception e) {
            sd = LocalDateTime.MIN;
        }
        try {
            ed = LocalDateTime.parse(endDate);
        } catch (Exception e) {
            ed = LocalDateTime.MAX;
        }
        LocalTime st = null;
        LocalTime et = null;
        try {
            st = LocalTime.parse(startTime);
        } catch (Exception e) {
            st = LocalTime.MIN;
        }
        try {
            et = LocalTime.parse(endTime);
        } catch (Exception e) {
            et = LocalTime.MAX;
        }

        if (sd == LocalDateTime.MIN && ed == LocalDateTime.MAX && st == LocalTime.MIN && et == LocalTime.MAX) {
            log.info("getAll");
            return MealsUtil.getTos(service.getAll(new SecurityUtil().getAuthUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY);
        }
        log.info("getAllFiltered");
        return MealsUtil.getFilteredTos(service.getAllFiltered(new SecurityUtil().getAuthUserId(), sd, ed, st, et), MealsUtil.DEFAULT_CALORIES_PER_DAY, st, et);
    }

    public Meal get(int id) {
        log.info("get {}", id);
        return service.get(id, new SecurityUtil().getAuthUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        return service.create(meal, new SecurityUtil().getAuthUserId());
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id, new SecurityUtil().getAuthUserId());
    }

    public void update(Meal meal, int mealId) {
        log.info("update {} with id={}", meal, mealId);
        assureIdConsistent(meal, mealId);
        service.update(meal, new SecurityUtil().getAuthUserId());
    }

    public void printLogAboutService() {
        log.info("***Current service***: {}", service);
    }
}