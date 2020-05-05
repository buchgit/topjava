package ru.javawebinar.topjava.util;

import com.sun.org.apache.xpath.internal.objects.XNull;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.net.URLStreamHandler;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

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

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println("------------------------------------------------------------------------------");

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
        SortedMap<LocalDate, Integer> map = new TreeMap<>();

        BinaryOperator<Integer> summer = (i, j) -> i + j;

        Consumer<UserMeal> toMapper = (userMeal) -> {
            if (map.containsKey(userMeal.getLocalDate()))
                map.put(userMeal.getLocalDate(), summer.apply(map.get(userMeal.getLocalDate()), userMeal.getCalories()));
            else {
                map.put(userMeal.getLocalDate(), userMeal.getCalories());
            }
        };

        BiPredicate<LocalDate, Integer> moreThan = (localDate, caloriesPD) -> map.get(localDate).compareTo(caloriesPD) > 0;

        Function<UserMeal, UserMealWithExcess> function1 = (userMeal) -> {
            return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), moreThan.test(userMeal.getLocalDate(), caloriesPerDay));
        };

         class finishCollector implements Collector<UserMealWithExcess, List<UserMealWithExcess>,List<UserMealWithExcess>> {
            @Override
            public Supplier<List<UserMealWithExcess>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<UserMealWithExcess>, UserMealWithExcess> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<UserMealWithExcess>> combiner() {
                return (acc, ps) -> {
                    acc.addAll(ps);
                    return acc;
                };
            }
            @Override
            public Function<List<UserMealWithExcess>, List<UserMealWithExcess>> finisher() {
                return (userListIn)->{
                    List<UserMealWithExcess> tempList =  new ArrayList<>();
                    for (UserMealWithExcess u:userListIn){
                        u.setExcess(moreThan.test(u.getDateTime().toLocalDate(),caloriesPerDay));
                        tempList.add(u);
                    }
                    return tempList;
                };
            }
            @Override
            public Set<java.util.stream.Collector.Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));
            }
        }
        return meals.stream()
                .peek(toMapper)
                .filter(meal -> TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(function1)
                .collect(new finishCollector());
    }
}
