package ru.otus.hw1.dao;

import com.univocity.parsers.common.DataValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.utils.CsvReader;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerDaoImpl implements AnswerDao {

    private final List<Answer> answers;

    private String test;

    public AnswerDaoImpl(@Qualifier("answer") Resource answerResource) {
        this.answers = CsvReader.readData(answerResource, Answer.class);
    }

    @Override
    public List<Answer> getAll() {
        return answers;
    }

    @Override
    public Answer getById(int id) {
        return getAll().stream()
                .filter(a -> a.getId() == id)
                .findAny()
                .orElseThrow(() -> new DataValidationException("answer with id " + id + " not found"));
    }


    @Override
    public List<Answer> getByQuestionId(int id) {
        return getAll().stream()
                .filter(a -> a.getQuestionId() == id)
                .collect(Collectors.toList());
    }

    @Override
    public int getCount() {
        return answers.size();
    }
}
