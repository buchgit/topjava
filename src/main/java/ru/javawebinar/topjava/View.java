package ru.javawebinar.topjava;

import javax.validation.groups.Default;

public class View {
    public interface Persist extends Default {}
    public interface JsonREST {}
    public interface JsonUI {}

    public interface ValidatedUI {}
}