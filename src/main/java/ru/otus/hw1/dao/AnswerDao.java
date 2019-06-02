package ru.otus.hw1.dao;

import ru.otus.hw1.model.Answer;

import java.util.List;

public interface AnswerDao extends Dao<Answer> {

    List<Answer> getByQuestionId(int id);

}
