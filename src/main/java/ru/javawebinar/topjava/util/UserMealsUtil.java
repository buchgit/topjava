package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.*;
import java.util.*;
import java.util.function.*;
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
        System.out.println("-------------------------filteredByCycles-----------------------------------------------------");
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println("-------------------------filteredByStreams-----------------------------------------------------");
        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));      //раскомментить
    }

    //цикл, вариант 1, в 1 проход
    public static List<UserMealWithExcess> filteredByCycles3(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate,Integer>summing = new HashMap<>();
        List<UserMealWithExcess>trueList = new ArrayList<>();
        List<UserMealWithExcess>falseList = new ArrayList<>();
        List<UserMealWithExcess>resultList = new ArrayList<>();
        LocalDate previousDate = null;
        int count = meals.size();

        for (UserMeal userMeal : meals) {
            count-=1;
            if (!summing.containsKey(userMeal.getDate())&&count!=meals.size()-1){//переход даты
                boolean excess = summing.get(previousDate)>caloriesPerDay;
                if (excess){
                    resultList.addAll(trueList);
                }
                else{
                    resultList.addAll(falseList);
                }

                previousDate = userMeal.getDate();
                trueList.clear();
                falseList.clear();

                summing.merge(userMeal.getDate(),userMeal.getCalories(),Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(),startTime,endTime)){
                    trueList.add(convertToUserMealWithExcess(userMeal,true));
                    falseList.add(convertToUserMealWithExcess(userMeal,false));
                }
            }else {
                previousDate = userMeal.getDate();
                summing.merge(userMeal.getDate(),userMeal.getCalories(),Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(),startTime,endTime)){
                    trueList.add(convertToUserMealWithExcess(userMeal,true));
                    falseList.add(convertToUserMealWithExcess(userMeal,false));
                }
            }
            if (count==0){
                boolean excess = summing.get(userMeal.getDate())>caloriesPerDay;
                if (excess){
                    resultList.addAll(trueList);
                }
                else{
                    resultList.addAll(falseList);
                }
            }
        }
        return resultList;
    }
    //цикл, вариант 2, в 2 прохода
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> map = new HashMap<>();

        for (UserMeal userMeal : meals) {
            map.merge(userMeal.getDate(), userMeal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> list = new ArrayList<>();
        for (UserMeal userMeal : meals) {
            if (userMeal != null) {
                if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime))
                    list.add(convertToUserMealWithExcess(userMeal,map.get(userMeal.getDate())>caloriesPerDay));
            }
        }
        return list;
    }
    //стрим, вариант 1,в 1 проход
    public static List<UserMealWithExcess> filteredByStreams3(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class FinishCollector implements Collector<UserMeal, Map<List<UserMeal>, Boolean>, List<UserMealWithExcess>> {
            boolean excess;
            final Integer[] calories = {0};
            final Integer[] count = {meals.size()};
            final LocalDate[] previousDate = {null};
            ArrayList<UserMeal> tempList = new ArrayList<>();

            @Override
            public Supplier<Map<List<UserMeal>, Boolean>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<List<UserMeal>, Boolean>, UserMeal> accumulator() {
                return (acc, userMeal) -> {
                    count[0]-=1;
                    if (!userMeal.getDate().equals(previousDate[0]) && previousDate[0] != null) {//переход даты
                        excess = (calories[0] > caloriesPerDay);
                        ArrayList<UserMeal>key = new ArrayList<>();
                        key.addAll(tempList);
                        acc.put(key, excess);
                        tempList.clear();
                        if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime)) {
                            tempList.add(userMeal);
                        }
                        calories[0] = userMeal.getCalories();
                        previousDate[0] = userMeal.getDate();
                    } else {
                        previousDate[0] = userMeal.getDate();
                        if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime)) {
                            tempList.add(userMeal);
                        }
                        calories[0] += userMeal.getCalories();
                    }
                    if ((count[0]==0)){
                        excess = (calories[0] > caloriesPerDay);
                        ArrayList<UserMeal>key = new ArrayList<>();
                        key.addAll(tempList);
                        acc.put(key, excess);
                    }
                };
            }

            @Override
            public BinaryOperator<Map<List<UserMeal>, Boolean>> combiner() {
                return (map, map1) -> Stream.concat(
                        map.entrySet().stream(),
                        map1.entrySet().stream())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            }

            @Override
            public Function<Map<List<UserMeal>, Boolean>, List<UserMealWithExcess>> finisher() {
                return maps -> maps.entrySet().stream()
                        .map(e -> e.getKey().stream().map(userMeal -> {
                            return convertToUserMealWithExcess(userMeal, e.getValue());
                        }).collect(Collectors.toList()))
                        .flatMap(list -> list.stream())
                        .collect(Collectors.toList())
                        ;
            }

            @Override
            public Set<java.util.stream.Collector.Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));
            }
        } //class
        return meals.stream()
                .collect(new FinishCollector());
    }
    //стрим, вариант 2, в 1 проход
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        class FinishCollector implements Collector<UserMeal, Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>, List<UserMealWithExcess>> {

            private Map<LocalDate, Integer> map = new HashMap<>();

            @Override
            public Supplier<Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>, UserMeal> accumulator() {
                return (listMap, userMeal) -> {
                    LocalDate day = userMeal.getDate();
                    map.merge(day, userMeal.getCalories(), Integer::sum);

                    if (TimeUtil.isBetweenHalfOpen(userMeal.getTime(), startTime, endTime)) {
                         if (!listMap.containsKey(day)) {
                            ArrayList tempList = new ArrayList();
                            ArrayList<UserMealWithExcess> listWithTrue = new ArrayList<>();
                            ArrayList<UserMealWithExcess> listWithFalse = new ArrayList<>();
                            tempList.add(0, listWithFalse);
                            tempList.add(1, listWithTrue);
                            listMap.put(day, tempList);
                        }
                        listMap.get(day).get(0).add(convertToUserMealWithExcess(userMeal, false));
                        listMap.get(day).get(1).add(convertToUserMealWithExcess(userMeal, true));
                        if ((map.get(day).compareTo(caloriesPerDay) > 0)) {
                            ArrayList tempList = new ArrayList();
                            ArrayList<UserMealWithExcess> listWithFalse = new ArrayList<>(0);
                            tempList.add(0, listWithFalse);
                            tempList.add(1, listMap.get(day).get(1));
                            listMap.put(day, tempList);
                        }
                    }
                };
            }

            @Override
            public BinaryOperator<Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>> combiner() {
                return (acc, ps) -> ps.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(),
                        (list1, list2) -> {
                            list1.get(0).addAll(list2.get(0));
                            list1.get(1).addAll(list2.get(1));
                            return list1;
                        }));
            }

            @Override
            public Function<Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>, List<UserMealWithExcess>> finisher() {
                return map -> map
                        .values()
                        .stream()
                        .map(list -> {
                            return list.get(0).size() < list.get(1).size() ?list.get(1):list.get(0);
                        })
                        .flatMap(list -> list.stream())
                        .collect(Collectors.toList())
                        ;
            }

            @Override
            public Set<java.util.stream.Collector.Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));
            }
        }
        return meals.stream().collect(new FinishCollector());
    }

    private static UserMealWithExcess convertToUserMealWithExcess(UserMeal userMeal, boolean excess){
        return new UserMealWithExcess(userMeal.getDateTime(), userMeal.getDescription(), userMeal.getCalories(), excess);
    }

}
