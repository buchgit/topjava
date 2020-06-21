package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
    public static final int NOT_FOUND = 10;
    public static final int MEAL_ID = START_SEQ + 2;
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;
    public static final LocalDate START_DATE = LocalDate.of(2020,1,29);
    public static final LocalDate END_DATE = LocalDate.of(2020,1,30);

    public static final List<Meal> MEALS = Arrays.asList(
            new Meal(MEAL_ID + 6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410),
            new Meal(MEAL_ID + 5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(MEAL_ID + 4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(MEAL_ID + 3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(MEAL_ID + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(MEAL_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(MEAL_ID + 0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500)
    );

    public static final List<Meal> MEALS_FILTERED = Arrays.asList(
            new Meal(MEAL_ID + 2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(MEAL_ID + 1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(MEAL_ID + 0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500)
    );

    public static final Meal MEAL = new Meal(MEAL_ID,LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);

    public static Meal getNew() {
        return new Meal(MEAL_ID,LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(MEAL);
        updated.setDescription("update description");
        updated.setCalories(300);
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected,"user_id");
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields("user_id").isEqualTo(expected);
    }
}
