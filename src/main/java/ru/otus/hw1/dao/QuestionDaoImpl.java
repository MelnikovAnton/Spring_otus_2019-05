package ru.otus.hw1.dao;


import com.univocity.parsers.common.DataValidationException;
import org.springframework.core.io.Resource;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.utils.CsvReader;

import java.util.List;

public class QuestionDaoImpl implements QuestionDao{

     private final List<Question> questions;

    public QuestionDaoImpl(Resource questionResource) {
       this.questions= CsvReader.readData(questionResource,Question.class);
    }


    @Override
    public List<Question> getAll() {
       return questions;
    }

    @Override
    public Question getById(int id) throws DataValidationException {
        return getAll().stream()
                .filter(a->a.getId()==id)
                .findAny()
                .orElseThrow(()->new DataValidationException("question with id " + id+ " not found"));
    }

    @Override
    public int getCount() {
        return questions.size();
    }
}
