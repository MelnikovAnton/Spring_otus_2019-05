package ru.otus.hw1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
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
@SpringBootTest
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

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    void readById(int id) throws DataValidationException {
        QuestionDao questionDao = getQuestionDaoMock();
        AnswerDao answerDao = getEmptyAnswerDao();
        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);
        String q = service.readById(id).getQuestion();
        assertEquals("Test" + id, q);
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    void getAnswers(int id) throws DataValidationException {
        QuestionDao questionDao = getQuestionDaoMock();
        AnswerDao answerDao = getAnswersDaoMock();
        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);
        List<Answer> answers = service.getAnswers(id);
        assertEquals(answers.get(0).getId(), id * 3);
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8, 9})
    void getCorrectAnswer(int id) throws DataValidationException {
        QuestionDao questionDao = getQuestionDaoMock();
        AnswerDao answerDao = getAnswersDaoMock();
        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);
        Answer answer = service.getCorrectAnswer(id);
        assertEquals(answer.getAnswer(), "ans1");
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