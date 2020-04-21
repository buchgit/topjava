package ru.javawebinar.topjava.util;

import jdk.nashorn.internal.objects.annotations.Function;
import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        Collections.sort(meals,userMealComparator);
        SortedMap<Integer,Integer>map = new TreeMap<>();
        List<UserMealWithExcess> list = new ArrayList<>();

        int sum = 0;
        for (UserMeal userMeal:meals){
            if(userMeal==null)continue;
            Integer key;
            if (map.containsKey(key=userMeal.getDateTime().getDayOfYear()))
                map.put(key,sum+=userMeal.getCalories());
            else {
                sum = userMeal.getCalories();
                map.put(userMeal.getDateTime().getDayOfYear(),sum);
            }
        }
        for (UserMeal userMeal:meals){
            if(userMeal!=null){
                if (!TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(),startTime,endTime))continue;
            }else continue;
            list.add(new UserMealWithExcess(userMeal.getDateTime(),userMeal.getDescription(),userMeal.getCalories(),map.get(userMeal.getDateTime().getDayOfYear())>caloriesPerDay));
        }
        return list;
    }

    public static Comparator<UserMeal> userMealComparator = (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime());

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<Integer,Integer> map= meals.stream()
        .filter(e->e!=null)
        .collect(Collectors.toMap(e->e.getDateTime().getDayOfYear(),e-> e.getCalories()  ,(sum, curvalue) -> sum+curvalue));

        Map<UserMealWithExcess,Boolean> map2= meals.stream()
        .collect(Collectors.toMap(e->new UserMealWithExcess(e.getDateTime(),e.getDescription(),e.getCalories(),false),e->(map.get(e.getDateTime().getDayOfYear())>caloriesPerDay)));

        List<UserMealWithExcess> list = map2.entrySet().stream()
        .map(e->new UserMealWithExcess(e.getKey().getDateTime(),e.getKey().getDescription(),e.getKey().getCalories(),e.getValue()))
        .filter(e->TimeUtil.isBetweenHalfOpen(e.getDateTime().toLocalTime(),startTime,endTime))
        .collect(Collectors.toList());

        return list;
    }
}
