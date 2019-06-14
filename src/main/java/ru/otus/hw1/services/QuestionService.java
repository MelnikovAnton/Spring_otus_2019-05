package ru.otus.hw1.services;

import org.springframework.core.io.Resource;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.utils.DataValidationException;

import java.util.List;

public interface QuestionService {

    List<Question> readAll();

    Question readById(int id) throws DataValidationException;

    Question getRandom() throws DataValidationException;

    List<Answer> getAnswers(int id);

    Answer getCorrectAnswer(int id);

    void reloadQuestions(Resource locale);

    void reloadAnswer(Resource localized);
}
