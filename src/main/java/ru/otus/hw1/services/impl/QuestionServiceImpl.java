package ru.otus.hw1.services.impl;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.otus.hw1.config.Config;
import ru.otus.hw1.dao.AnswerDao;
import ru.otus.hw1.dao.QuestionDao;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.services.QuestionService;
import ru.otus.hw1.utils.DataValidationException;

import java.net.URL;
import java.util.List;
import java.util.Random;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao questionDao;
    private final AnswerDao answerDao;


    final Random random = new Random();

    public QuestionServiceImpl(QuestionDao questionDao, AnswerDao answerDao) {
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }

    @Override
    public List<Question> readAll() {
        return questionDao.getAll();
    }

    @Override
    public Question readById(int id) throws DataValidationException {
        return questionDao.getById(id);
    }

    @Override
    public Question getRandom() throws DataValidationException {
        int count = questionDao.getCount();
        return questionDao.getById(random.nextInt(count)+1);
    }

    @Override
    public List<Answer> getAnswers(int questionId) {
        return answerDao.getByQuestionId(questionId);
    }

    @Override
    public Answer getCorrectAnswer(int questionId) {
        return answerDao.getByQuestionId(questionId).stream()
                .filter(Answer::isCorrect)
                .findFirst().get();
    }

    @Override
    public void reloadQuestions(Resource localized) {
        questionDao.reloadData(localized);
    }

    @Override
    public void reloadAnswer(Resource localized){
        answerDao.reloadData(localized);
    }


}
