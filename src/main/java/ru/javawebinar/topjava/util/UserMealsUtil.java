package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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

    //в 2 прохода
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Collections.sort(meals, userMealComparator);
        SortedMap<Integer, Integer> map = new TreeMap<>();
        List<UserMealWithExcess> list = new ArrayList<>();

        int sum = 0;
        for (UserMeal userMeal : meals) {
            if (userMeal == null) continue;
            int key;
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

    //в 1 проход
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        class finishCollector implements Collector<UserMeal, Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>, List<UserMealWithExcess>> {

            private Map<LocalDate, Integer> map = new TreeMap<>();

            BiPredicate<LocalDate, Integer> aboveTheNorm = (localDate, calories) -> map.get(localDate).compareTo(calories) > 0;

            @Override
            public Supplier<Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>> supplier() {
                return TreeMap::new;
            }

            @Override
            public BiConsumer<Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>, UserMeal> accumulator() {
                return (listMap, userMeal) -> {
                    //суммируем по дням
                    map.merge(userMeal.getLocalDate(), userMeal.getCalories(), Integer::sum);
                    LocalDate day = userMeal.getLocalDate();
                    if (aboveTheNorm.test(day, caloriesPerDay)) {
                        listMap.get(day).get(0).clear();
                    }
                    //добавляем отфильтрованные элементы в два листа: с excess = true и excess = false
                    if (TimeUtil.isBetweenHalfOpen(userMeal.getLocalTime(), startTime, endTime)) {
                        LocalDate key;
                        //проверяем, есть ли запись с таким ключом
                        if (listMap.containsKey(day)) {
                            //если на текущем элементе есть превышение по норме калорий, то заполняется только лист true
                            if (!aboveTheNorm.test(day, caloriesPerDay)) {
                                listMap.get(day).get(0).add(new UserMealWithExcess(userMeal, false));
                            }
                            listMap.get(day).get(1).add(new UserMealWithExcess(userMeal, true));
                        } else {//записи с ключом нет,создаем новый элемент Map
                            ArrayList tempList = new ArrayList();
                            ArrayList<UserMealWithExcess> listWithTrue = new ArrayList<>();
                            ArrayList<UserMealWithExcess> listWithFalse = new ArrayList<>();
                            if (!aboveTheNorm.test(day, caloriesPerDay)) {
                                listWithFalse.add(new UserMealWithExcess(userMeal, false));
                            }
                            listWithTrue.add(new UserMealWithExcess(userMeal, true));
                            tempList.add(0, listWithFalse);
                            tempList.add(1, listWithTrue);
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
                            if (list.get(0).size() > 0) return list.get(0);
                            else return list.get(1);
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
        return meals.stream().collect(new finishCollector());
    }
}
