package ru.otus.hw1.services;

import ru.otus.hw1.utils.DataValidationException;

public interface DataValidator {

    void validate() throws DataValidationException;
}
