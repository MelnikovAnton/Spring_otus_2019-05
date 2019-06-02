package ru.otus.hw1.dao;

import ru.otus.hw1.utils.DataValidationException;

import java.util.List;

public interface Dao<T> {
    List<T> getAll();
    T getById(int id) throws DataValidationException;
    int getCount();
}
