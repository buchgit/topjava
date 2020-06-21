package ru.javawebinar.topjava.service;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))

public class MealServiceTest extends TestCase {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Autowired
    private MealRepository repository;

    @Test
    public void testGet() throws Exception {
        Meal meal = service.get(MEAL_ID,USER_ID);
        assertMatch(meal, MEAL);
    }
    @Test
    public void testGetNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND,USER_ID));
    }
    @Test
    public void testGetNotOwnMeal() throws Exception {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID,ADMIN_ID));
    }
    @Test
    public void testDelete() {
        service.delete(MEAL_ID,USER_ID);
        assertNull(repository.get(MEAL_ID,USER_ID));
    }
    @Test
    public void testDeletedNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_FOUND,USER_ID));
    }

    @Test
    public void testGetBetweenInclusive() {
        List<Meal> all = service.getBetweenInclusive(START_DATE,END_DATE,USER_ID);
        assertMatch(all, MEALS_FILTERED);
    }

    @Test
    public void testGetAll() {
        List<Meal> all = service.getAll(USER_ID);
        assertMatch(all, MEALS);
    }

    @Test
    public void testUpdate() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(MEAL_ID, USER_ID), updated);
    }
    @Test
    public void testUpdateNotOwnMeal() {
        Meal updated = getUpdated();
        assertThrows(NotFoundException.class, () -> service.update(updated, ADMIN_ID));
    }

    @Test
    public void testCreate() {
        Meal newMeal = getNew();
        Meal created = service.create(newMeal, USER_ID);
        Integer newId = created.getId();
        newMeal.setId(newId);
        assertMatch(created, newMeal);
        assertMatch(service.get(newId, USER_ID), newMeal);
    }
}