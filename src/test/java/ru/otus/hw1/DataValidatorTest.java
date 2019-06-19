package ru.otus.hw1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.jline.ScriptShellApplicationRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw1.dao.AnswerDao;
import ru.otus.hw1.dao.Dao;
import ru.otus.hw1.dao.QuestionDao;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.services.DataValidator;
import ru.otus.hw1.services.impl.DataValidatorImpl;
import ru.otus.hw1.utils.DataValidationException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false"})
@ActiveProfiles(profiles = "shell")
class DataValidatorTest {

    @Autowired
    private DataValidator dataValidator;


    @Test
    void validateWithSpringContext() {
        assertDoesNotThrow(dataValidator::validate);
    }

    @Test
    @DisplayName("Проверка не уникальных вопросов.")
    void validateNoUnicQuestionId() {
        Dao<Question> questionDao = getNotUnicQuestionId();
        Dao<Answer> answerDao = getEmptyAnswerDao();

        DataValidator dataValidator = new DataValidatorImpl(questionDao, answerDao);

        DataValidationException exception = assertThrows(DataValidationException.class, dataValidator::validate);
        assertEquals("Question IDs is not unique", exception.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка наличия более одного правильного ответа.")
    void validateNoCorrectAnswer() {

        Dao<Question> questionDao = getOneQuestion();
        Dao<Answer> answerDao = getNoCorrectAnswer();
        DataValidator dataValidator = new DataValidatorImpl(questionDao, answerDao);

        DataValidationException exception = assertThrows(DataValidationException.class, dataValidator::validate);
        assertEquals("SingleChoice question correct answer is not uniq", exception.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Проверка отсутствия ответа.")
    void validateNoAnswer() {
        Dao<Question> questionDao = getOneQuestion();
        Dao<Answer> answerDao = getEmptyAnswerDao();
        DataValidator dataValidator = new DataValidatorImpl(questionDao, answerDao);

        DataValidationException exception = assertThrows(DataValidationException.class, dataValidator::validate);
        assertEquals("SingleChoice question has less or equals than one answer", exception.getMessage(), exception.getMessage());
    }


    private QuestionDao getNotUnicQuestionId() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(1, "Test1"));
        questions.add(new Question(1, "Test2"));
        QuestionDao questionDao = mock(QuestionDao.class);
        when(questionDao.getAll()).thenReturn(questions);
        return questionDao;
    }

    private AnswerDao getEmptyAnswerDao() {
        List<Answer> answers = new ArrayList<>();
        AnswerDao answerDao = mock(AnswerDao.class);
        when(answerDao.getAll()).thenReturn(answers);
        return answerDao;
    }

    private QuestionDao getOneQuestion() {
        List<Question> questions = new ArrayList<>();
        questions.add(new Question(1, "Test1"));
        QuestionDao questionDao = mock(QuestionDao.class);
        when(questionDao.getAll()).thenReturn(questions);
        return questionDao;
    }

    private AnswerDao getNoCorrectAnswer() {
        List<Answer> answers = new ArrayList<>();
        answers.add(new Answer(1, 1, false, "Ans1"));
        answers.add(new Answer(2, 1, false, "Ans2"));
        answers.add(new Answer(3, 1, false, "Ans3"));
        AnswerDao answerDao = mock(AnswerDao.class);
        when(answerDao.getAll()).thenReturn(answers);
        return answerDao;
    }
}