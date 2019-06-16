package ru.otus.hw1.services;

import ru.otus.hw1.model.User;

import java.io.InputStream;
import java.io.OutputStream;

public interface Quiz {

    void initQuiz(InputStream in, OutputStream out);

    User startQuiz(User user);

    void printResult(User user);

    int getResult(User user);

    void startQuiz();
}
