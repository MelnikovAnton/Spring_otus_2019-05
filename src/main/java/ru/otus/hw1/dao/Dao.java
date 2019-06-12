package ru.otus.hw1.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import ru.otus.hw1.config.Config;
import ru.otus.hw1.utils.DataValidationException;

import java.net.URL;
import java.util.List;


public interface Dao<T> {

    static final Logger log = LoggerFactory.getLogger(Dao.class);

    List<T> getAll();

    T getById(int id) throws DataValidationException;

    int getCount();

    void reloadData(Resource resource);

    default String checkLocalized(String resourceName, String locale) {
        String localized = resourceName.replace(".csv", "_" + locale + ".csv");
        URL u = Config.class.getResource("/" + localized);
        if (u == null) {
            log.warn("No localized resources: {}. Return default {}", localized, resourceName);
            return resourceName;
        } else return localized;
    }
}
