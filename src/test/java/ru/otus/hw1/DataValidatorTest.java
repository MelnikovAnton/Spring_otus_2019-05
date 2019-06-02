package ru.otus.hw1;

import org.junit.jupiter.api.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw1.dao.AnswerDao;
import ru.otus.hw1.dao.QuestionDao;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.services.DataValidator;
import ru.otus.hw1.services.impl.DataValidatorImpl;
import ru.otus.hw1.utils.DataValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataValidatorTest {
    
    @Test
    void validateWithSpringContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/validation-context.xml");
        DataValidator dataValidator = (DataValidator) context.getBean("dataValidator");
        assertDoesNotThrow(dataValidator::validate);
    }

    @Test
    void validateNoUnicQuestionId() {
        List<Question> questions= new ArrayList<>();
        questions.add(new Question(1,"Test1"));
        questions.add(new Question(1,"Test2"));
        List<Answer> answers= new ArrayList<>();

        AnswerDao answerDao = mock(AnswerDao.class);
        QuestionDao questionDao = mock(QuestionDao.class);
        when(answerDao.getAll()).thenReturn(answers);
        when(questionDao.getAll()).thenReturn(questions);

        DataValidator dataValidator = new DataValidatorImpl(questionDao,answerDao);

        DataValidationException exception = assertThrows(DataValidationException.class, dataValidator::validate);
        assertEquals("Question IDs is not unique", exception.getMessage(), exception.getMessage());
    }

    @Test
    void validateNoCorrectAnswer() {
        List<Question> questions= new ArrayList<>();
        questions.add(new Question(1,"Test1"));

        List<Answer> answers= new ArrayList<>();
        answers.add(new Answer(1,1,false,"Ans1"));
        answers.add(new Answer(2,1,false,"Ans2"));
        answers.add(new Answer(3,1,false,"Ans3"));

        AnswerDao answerDao = mock(AnswerDao.class);
        QuestionDao questionDao = mock(QuestionDao.class);
        when(answerDao.getAll()).thenReturn(answers);
        when(questionDao.getAll()).thenReturn(questions);

        DataValidator dataValidator = new DataValidatorImpl(questionDao,answerDao);

        DataValidationException exception = assertThrows(DataValidationException.class, dataValidator::validate);
        assertEquals("SingleChoice question correct answer is not uniq", exception.getMessage(), exception.getMessage());
    }

    @Test
    void validateNoAnswer() {
        List<Question> questions= new ArrayList<>();
        questions.add(new Question(1,"Test1"));
        List<Answer> answers= new ArrayList<>();
        AnswerDao answerDao = mock(AnswerDao.class);
        QuestionDao questionDao = mock(QuestionDao.class);
        when(answerDao.getAll()).thenReturn(answers);
        when(questionDao.getAll()).thenReturn(questions);

        DataValidator dataValidator = new DataValidatorImpl(questionDao,answerDao);

        DataValidationException exception = assertThrows(DataValidationException.class, dataValidator::validate);
        assertEquals("SingleChoice question has less or equals than one answer", exception.getMessage(), exception.getMessage());
    }
}