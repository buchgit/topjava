package ru.javawebinar.topjava.util;

import java.util.function.*;

public class Test {
//    @FunctionalInterface
//    public interface Supplier<T> {
//        T get();
//    }
//
//    @FunctionalInterface
//    public interface Consumer<T> {
//        void accept(T t);
//    }
//
//    @FunctionalInterface
//    public interface Function<T, R> {
//        R apply(T t);
//    }
//
//    @FunctionalInterface
//    public interface Predicate<T> {
//        boolean test(T t);
//    }
//
//    @FunctionalInterface
//    public interface UnaryOperator<T> extends Function<T, T> {
//
//    }


    public static void main(String[] args) {
        String t = "abcDeF";

        Supplier<Integer> resupplier = ()->t.length();
        System.out.println(resupplier.get());

        Supplier<char[]> supplier = ()-> t.toCharArray();

        Consumer<char[]> consumer = r->System.out.println("*"+r.length);
        Consumer<char[]>consumer1 = r->System.out.println("**"+ (r.length-1));

        consumer.andThen(consumer1).accept(supplier.get());

        Function<Integer,String> function = (i)->Integer.toString(i);
        String ss = "***"+function.apply(resupplier.get());

        Consumer<String>consumer2 = (r)->System.out.println(r);
        consumer2.accept(ss);

        Function<String,String>function1 = Function.identity();
        System.out.println(function1.apply("Some fraze"));

        Function<String, String> f1 = s -> s + "1";
        Function<String, String> f2 = s -> s + "2";
        Function<String, String> f3 = s -> s + "3";
        Function<String, String> f4 = s -> s + "4";
        System.out.println(f1.compose(f2).compose(f3).compose(f4).apply("Compose"));
        System.out.println(f1.andThen(f2).andThen(f3).andThen(f4).apply("AndThen"));

        Predicate<Integer>predicate = integer -> integer>0;
        System.out.println(predicate.test(-1));//false

        Predicate<Integer>predicate1 = integer -> integer<10;
        System.out.println(predicate.and(predicate1).test(2));//true
        System.out.println(predicate.or(predicate1).test(11));//true

        UnaryOperator <String>unaryOperator = s -> s+s+s;
        System.out.println(unaryOperator.apply(t));//abcDeFabcDeFabcDeF

        /* PRINT TO CONSOLE
        6
        *6
        **5
        ***6
        Some fraze
        Compose4321
        AndThen1234
        false
        true
        true
        abcDeFabcDeFabcDeF
         */
    }
}

