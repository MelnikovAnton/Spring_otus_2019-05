package ru.otus.hw1.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw1.dao.Dao;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.services.DataValidator;
import ru.otus.hw1.utils.DataValidationException;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataValidatorImpl implements DataValidator {

    private final Dao<Question> questionDao;
    private final Dao<Answer> answerDao;

    public DataValidatorImpl(Dao<Question> questionDao, Dao<Answer> answerDao) {
        this.questionDao = questionDao;
        this.answerDao = answerDao;
    }


    @PostConstruct
    public void start() {
        try {
            log.info("Start Data validation");
            validate();
        } catch (DataValidationException e) {
            System.out.println("Целостность данных нарушена. Запуск программы не возможен.");
            log.error("Data validation Error! Stop program.", e);
            System.exit(0);
        }
    }

    @Override
    public void validate() throws DataValidationException {
        List<Question> questions = questionDao.getAll();
        List<Answer> answers = answerDao.getAll();
        checkIds(questions);

        for (Question q : questions) {
            int id = q.getId();
            List<Answer> ans = answers.stream()
                    .filter(a -> a.getQuestionId() == id)
                    .collect(Collectors.toList());
            checkAnswers(ans);
        }
    }

    private void checkIds(List<Question> questions) throws DataValidationException {
        Set<Integer> set = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toSet());
        if (set.size() != questions.size()) {
            throw new DataValidationException("Question IDs is not unique");
        }
    }

    private boolean checkAnswers(List<Answer> answers) throws DataValidationException {
        boolean sizeCheck = answers.size() > 1;
        boolean correctCheck = answers.stream()
                .filter(Answer::isCorrect).count() == 1;
        if (!sizeCheck) {
            throw new DataValidationException("SingleChoice question has less or equals than one answer");
        }
        if (!correctCheck) {
            throw new DataValidationException("SingleChoice question correct answer is not uniq");
        }
        return true;
    }

}
