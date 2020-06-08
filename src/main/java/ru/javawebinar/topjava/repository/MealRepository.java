package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MealRepository {

    private int id;

    private Map<Integer, Meal> repository = new ConcurrentHashMap<>();

    public MealRepository() {
        List<Meal> list = Arrays.asList(
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500, id++),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000, id++),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500, id++),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100, id++),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000, id++),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500, id++),
                new Meal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410, id++)
        );
        for (int i = 0; i < list.size(); i++) {
            repository.put(list.get(i).getId(), list.get(i));
        }
    }

    public void add(Meal meal) {
        Integer id = generateId();
        meal.setId(id);
        repository.put(id, meal);
    }

    public boolean existById(int id) {
        return repository.containsKey(id);
    }

    public void deleteById(int id) {
        try {
            repository.remove(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(int Id, Meal meal) {
        repository.put(Id, meal);
    }

    public List<Meal> getAll() {
        List<Meal> list = new ArrayList<>();
        for (Map.Entry<Integer, Meal> p : repository.entrySet()) {
            list.add(p.getValue());
        }
        return Collections.unmodifiableList(list);
    }

    public Meal getById(int id) {
        return repository.get(id);
    }

    private Integer generateId() {
        String s = UUID.randomUUID().toString();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(s);
        Integer result = null;
        int start = 0;
        while (matcher.find(start)) {
            String value = s.substring(matcher.start(), matcher.end());
            result = Integer.parseInt(value);
            start = matcher.end();
        }
        while (repository.containsKey(result)) {
            id = generateId();
        }
        return result;
    }
}
