package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealPackaged;
import ru.javawebinar.topjava.model.UserMealWithExcess;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

public class UserMealsUtil {
    public static void main(String[] args) {

        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
        System.out.println("-------------------------filteredByCycles-----------------------------------------------------");
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println("-------------------------filteredByStreams-----------------------------------------------------");
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));      //раскомментить
    }


    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Collections.sort(meals, userMealComparator);
        SortedMap<Integer, Integer> map = new TreeMap<>();
        List<UserMealWithExcess> list = new ArrayList<>();

        int sum = 0;
        for (UserMeal userMeal : meals) {
            if (userMeal == null) continue;
            Integer key;
            if (map.containsKey(key = userMeal.getDateTime().getDayOfYear()))
                map.put(key, sum += userMeal.getCalories());
            else {
                sum = userMeal.getCalories();
                map.put(userMeal.getDateTime().getDayOfYear(), sum);
            }
        }
        for (UserMeal userMeal : meals) {
            if (userMeal != null) {
                if (!TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime)) continue;
            } else continue;
            list.add(new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), map.get(userMeal.getDateTime().getDayOfYear()) > caloriesPerDay));
        }
        return list;
    }

    public static Comparator<UserMeal> userMealComparator = (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime());

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Function<UserMeal, UserMealPackaged> packaging = (userMeal) -> {
            return new UserMealPackaged(userMeal, startTime, endTime);
        };

        //accumulator for collector
        class CustomAcc {
            private List<UserMealWithExcess> list = new ArrayList();
            private SortedMap<LocalDate, Integer> map = new TreeMap<>();

            public CustomAcc() {
            }

            public void add(UserMealPackaged userMealPackaged) {
                UserMealWithExcess u = new UserMealWithExcess(
                        userMealPackaged.getUserMeal().getDateTime(),
                        userMealPackaged.getUserMeal().getDescription(),
                        userMealPackaged.getUserMeal().getCalories(),
                        false
                );
                if (TimeUtil.isBetweenHalfOpen(userMealPackaged.getUserMeal().getLocalTime(), userMealPackaged.getStartTime(), userMealPackaged.getEndTime())) {
                    list.add(u);
                }
                BinaryOperator<Integer> summer = (i, j) -> i + j;
                if (map.containsKey(userMealPackaged.getUserMeal().getLocalDate()))
                    map.put(userMealPackaged.getUserMeal().getLocalDate(), summer.apply(map.get(userMealPackaged.getUserMeal().getLocalDate()), u.getCalories()));
                else {
                    map.put(userMealPackaged.getUserMeal().getLocalDate(), u.getCalories());
                }
            }

            public void addAll(CustomAcc customAcc) {
                BinaryOperator<Integer> summer = (i, j) -> i + j;
                for (UserMealWithExcess u : customAcc.getList()) {
                    list.add(u);
                }
                for (LocalDate key : customAcc.getMap().keySet()) {
                    if (map.containsKey(key))
                        map.put(key, summer.apply(map.get(key), customAcc.getMap().get(key)));
                    else {
                        map.put(key, customAcc.getMap().get(key));
                    }
                }
            }

            public List<UserMealWithExcess> getList() {
                return list;
            }

            public Map<LocalDate, Integer> getMap() {
                return map;
            }

            public void setExcessFields(Integer calories) {
                BiPredicate<LocalDate, Integer> moreThan = (localDate, caloriesPD) -> map.get(localDate).compareTo(caloriesPD) > 0;
                for (UserMealWithExcess u : list) {
                    u.setExcess(moreThan.test(u.getLocalDate(), calories));
                }
            }
        }
        //collector
        class finishCollector implements Collector<UserMealPackaged, CustomAcc, List<UserMealWithExcess>> {

            @Override
            public Supplier<CustomAcc> supplier() {
                return CustomAcc::new;
            }

            @Override
            public BiConsumer<CustomAcc, UserMealPackaged> accumulator() {
                return CustomAcc::add;
            }

            @Override
            public BinaryOperator<CustomAcc> combiner() {
                return (acc, ps) -> {
                    acc.addAll(ps);
                    return acc;
                };
            }

            @Override
            public Function<CustomAcc, List<UserMealWithExcess>> finisher() {
                return (userListIn) -> {
                    userListIn.setExcessFields(caloriesPerDay);
                    return userListIn.getList();
                };
            }

            @Override
            public Set<java.util.stream.Collector.Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));
            }
        }
        return meals.stream().map(packaging).collect(new finishCollector());
    }
}
