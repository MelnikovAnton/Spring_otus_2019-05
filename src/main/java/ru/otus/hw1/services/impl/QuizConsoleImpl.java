package ru.otus.hw1.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import ru.otus.hw1.config.ApplicationSettings;
import ru.otus.hw1.services.QuestionService;

@Slf4j
public class QuizConsoleImpl extends QuizImpl {

    public QuizConsoleImpl(QuestionService questionService, MessageSource messageSource, ApplicationSettings settings) {
        super(questionService, messageSource, settings);
    }
}
