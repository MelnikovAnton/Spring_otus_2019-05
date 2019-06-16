package ru.otus.hw1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw1.dao.AnswerDao;
import ru.otus.hw1.dao.QuestionDao;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.services.QuestionService;
import ru.otus.hw1.services.impl.QuestionServiceImpl;
import ru.otus.hw1.utils.DataValidationException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false"})
@ActiveProfiles(profiles = "shell")
class QuestionServiceTest {

    @Autowired
    private QuestionService service;

    @Test
    void testWithSpringContext() {
        List<Question> all = assertDoesNotThrow(service::readAll);
        int id = all.get(0).getId();
        Question question = assertDoesNotThrow(() -> service.readById(id));
        assertEquals(all.get(0), question);
    }

    @TestFactory
    @DisplayName("Тест получения вопроса по Id")
    Stream<DynamicTest> readById() throws DataValidationException {
        QuestionDao questionDao = getQuestionDaoMock();
        AnswerDao answerDao = getEmptyAnswerDao();
        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);
        return IntStream.range(1, 10)
                .mapToObj(i -> DynamicTest.dynamicTest("Тест получения вопроса по Id " + i,
                        () -> {
                            String q = service.readById(i).getQuestion();
                            assertEquals("Test" + i, q);
                        }));
    }


    @TestFactory
    @DisplayName("Получение ответа по ID вопроса")
    Stream<DynamicTest> getAnswers() throws DataValidationException {
        QuestionDao questionDao = getQuestionDaoMock();
        AnswerDao answerDao = getAnswersDaoMock();
        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);
        return IntStream.range(1, 10)
                .mapToObj(i -> DynamicTest.dynamicTest("Получен ответ по вопросу ID " + i,
                        () -> {
                            List<Answer> answers = service.getAnswers(i);
                            assertEquals(answers.get(0).getId(), i * 3);
                        }));
    }


    @TestFactory
    @DisplayName("Получение правильного ответа.")
    Stream<DynamicTest> getCorrectAnswer() throws DataValidationException {
        QuestionDao questionDao = getQuestionDaoMock();
        AnswerDao answerDao = getAnswersDaoMock();
        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);
        return IntStream.range(1, 10)
                .mapToObj(i -> DynamicTest.dynamicTest("Получение правильного ответа." + i,
                        () -> {
                            Answer answer = service.getCorrectAnswer(i);
                            assertEquals(answer.getAnswer(), "ans1");
                        }));
    }


    private QuestionDao getQuestionDaoMock() throws DataValidationException {
        List<Question> questions = IntStream.range(0, 10)
                .mapToObj(i -> new Question(i, "Test" + i))
                .collect(Collectors.toList());
        QuestionDao questionDao = mock(QuestionDao.class);
        when(questionDao.getById(anyInt()))
                .thenAnswer(i -> questions.get(i.getArgument(0)));
        when(questionDao.getAll())
                .thenReturn(questions);
        when(questionDao.getCount()).thenReturn(questions.size());
        return questionDao;
    }

    private AnswerDao getAnswersDaoMock() throws DataValidationException {
        QuestionDao questionDao = getQuestionDaoMock();
        List<Question> questions = questionDao.getAll();
        List<Answer> answers = questions.stream()
                .map(Question::getId)
                .map(id -> Stream.of(new Answer(id * 3, id, true, "ans1"),
                        new Answer(id * 3 + 1, id, false, "ans2"),
                        new Answer(id * 3 + 2, id, false, "ans3")))
                .flatMap(Function.identity())
                .collect(Collectors.toList());

        AnswerDao answerDao = mock(AnswerDao.class);
        when(answerDao.getByQuestionId(anyInt())).thenAnswer(i -> answers.stream()
                .filter(a -> a.getQuestionId() == (Integer) i.getArgument(0))
                .collect(Collectors.toList()));
        return answerDao;
    }

    private AnswerDao getEmptyAnswerDao() {
        AnswerDao answerDao = mock(AnswerDao.class);
        return answerDao;
    }
}