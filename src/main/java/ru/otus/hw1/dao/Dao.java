package ru.otus.hw1.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import ru.otus.hw1.config.Config;
import ru.otus.hw1.utils.DataValidationException;

import java.net.URL;
import java.util.List;


public interface Dao<T> {

    List<T> getAll();

    T getById(int id) throws DataValidationException;

    int getCount();

    void reloadData(Resource resource);
}
