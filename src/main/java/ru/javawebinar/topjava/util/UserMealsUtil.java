package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    public static Comparator<UserMeal> userMealComparator = (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime());
    public static Comparator<UserMealWithExcess> userMealWithExcessComparator = (o1, o2) -> o1.getDateTime().compareTo(o2.getDateTime());

    //цикл, вариант 1, в 1 проход
    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        Collections.sort(meals, userMealComparator);
        SortedMap<LocalDate,Integer>summing = new TreeMap<>();
        List<UserMealWithExcess>trueList = new ArrayList<>();
        List<UserMealWithExcess>falseList = new ArrayList<>();
        List<UserMealWithExcess>resultList = new ArrayList<>();
        LocalDate previousDate = null;
        int count = meals.size();

        for (UserMeal userMeal : meals) {
            count-=1;
            if (!summing.containsKey(userMeal.getLocalDate())&&count!=meals.size()-1){//переход даты
                //записываем в результирующий лист
                boolean excess = summing.get(previousDate)>caloriesPerDay;
                if (excess){
                    resultList.addAll(trueList);
                }
                else{
                    resultList.addAll(falseList);
                }
                //для новой даты
                previousDate = userMeal.getLocalDate();
                trueList.clear();
                falseList.clear();
                //для первого элемента
                summing.merge(userMeal.getLocalDate(),userMeal.getCalories(),Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(userMeal.getLocalTime(),startTime,endTime)){
                    trueList.add(new UserMealWithExcess(userMeal,true));
                    falseList.add(new UserMealWithExcess(userMeal,false));
                }
            }else {//если дата прежняя или первый элемент списка
                previousDate = userMeal.getLocalDate();
                summing.merge(userMeal.getLocalDate(),userMeal.getCalories(),Integer::sum);
                if (TimeUtil.isBetweenHalfOpen(userMeal.getLocalTime(),startTime,endTime)){
                    trueList.add(new UserMealWithExcess(userMeal,true));
                    falseList.add(new UserMealWithExcess(userMeal,false));
                }
            }
            //для последнего элемента списка
            if (count==0){
                boolean excess = summing.get(userMeal.getLocalDate())>caloriesPerDay;
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
    public static List<UserMealWithExcess> filteredByCycles2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Collections.sort(meals, userMealComparator);
        SortedMap<LocalDate, Integer> map = new TreeMap<>();
        List<UserMealWithExcess> list = new ArrayList<>();

        for (UserMeal userMeal : meals) {
            map.merge(userMeal.getLocalDate(), userMeal.getCalories(), Integer::sum);
        }

        for (UserMeal userMeal : meals) {
            if (userMeal != null) {
                if (TimeUtil.isBetweenHalfOpen(userMeal.getDateTime().toLocalTime(), startTime, endTime))
                    list.add(new UserMealWithExcess(userMeal,map.get(userMeal.getLocalDate())>caloriesPerDay));
            }
        }
        return list;
    }
    //стрим, вариант 1,в 1 проход
    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

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
                    if (!userMeal.getLocalDate().equals(previousDate[0]) && previousDate[0] != null) {//переход даты
                        //формируем итоговый пакет на дату
                        excess = (calories[0] > caloriesPerDay);
                        ArrayList<UserMeal>key = new ArrayList<>();
                        key.addAll(tempList);
                        acc.put(key, excess);
                        //для новой даты и первого элемента
                        tempList.clear();
                        if (TimeUtil.isBetweenHalfOpen(userMeal.getLocalTime(), startTime, endTime)) {
                            tempList.add(userMeal);
                        }
                        calories[0] = userMeal.getCalories();
                        previousDate[0] = userMeal.getLocalDate();
                    } else {//дата не изменилась
                        previousDate[0] = userMeal.getLocalDate();
                        if (TimeUtil.isBetweenHalfOpen(userMeal.getLocalTime(), startTime, endTime)) {
                            tempList.add(userMeal);
                        }
                        calories[0] += userMeal.getCalories();
                    }
                    //для последнего элемента стрима
                    if ((count[0]==0)){
                        excess = (calories[0] > caloriesPerDay);
                        ArrayList<UserMeal>key = new ArrayList<>();
                        key.addAll(tempList);
                        acc.put(key, excess);
                    }
                };
            }

            //не используется
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
                            return new UserMealWithExcess(userMeal, e.getValue());
                        }).collect(Collectors.toList()))
                        .flatMap(list -> list.stream())
                        .sorted(userMealWithExcessComparator)
                        .collect(Collectors.toList())
                        ;
            }

            @Override
            public Set<java.util.stream.Collector.Characteristics> characteristics() {
                return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED));
            }
        } //class
        return meals.stream()
                .sorted(userMealComparator)
                .collect(new FinishCollector());
    }
    //стрим, вариант 2, в 1 проход
    public static List<UserMealWithExcess> filteredByStreams2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        class FinishCollector implements Collector<UserMeal, Map<LocalDate, ArrayList<ArrayList<UserMealWithExcess>>>, List<UserMealWithExcess>> {

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
                    //eсли превышение дневной нормы, очищаем один из 2-х листов
                    if (aboveTheNorm.test(day, caloriesPerDay)) {
                        listMap.get(day).get(0).clear();
                    }
                    //добавляем отфильтрованные элементы в два листа: с excess = true и excess = false
                    if (TimeUtil.isBetweenHalfOpen(userMeal.getLocalTime(), startTime, endTime)) {
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
        return meals.stream().collect(new FinishCollector());
    }
}
