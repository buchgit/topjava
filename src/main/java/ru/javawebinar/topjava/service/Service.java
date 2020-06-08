package ru.javawebinar.topjava.service;

import java.util.List;

public interface Service {
    <T> void add(T t);

    void delete(int id);

    <T> void update(int Id, T t) throws Exception;

    <T> List<T> getAll();

    <T> T getById(int id);
}
