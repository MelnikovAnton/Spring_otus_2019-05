package ru.otus.hw1;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw1.dao.AnswerDao;
import ru.otus.hw1.dao.QuestionDao;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.services.QuestionService;
import ru.otus.hw1.services.impl.QuestionServiceImpl;
import ru.otus.hw1.utils.DataValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionServiceTest {

    @Test
    void testWithSpringContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("question-context.xml");
        QuestionService service = context.getBean(QuestionService.class);
        List<Question> all = assertDoesNotThrow(service::readAll);
        int id = all.get(0).getId();
        Question question = assertDoesNotThrow(() -> service.readById(id));
        assertEquals(all.get(0), question);
    }

    @RepeatedTest(10)
    void readById() throws DataValidationException {
        List<Question> questions = IntStream.range(1, 10)
                .mapToObj(i -> new Question(i, "Test" + i))
                .collect(Collectors.toList());


        AnswerDao answerDao = mock(AnswerDao.class);
        QuestionDao questionDao = mock(QuestionDao.class);


        when(questionDao.getById(anyInt())).thenAnswer(i -> questions.get(i.getArgument(0)));


        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);
        int id = new Random().nextInt(9);
        String q = service.readById(id).getQuestion();
//        System.out.println(q);
        assertEquals("Test" + (id + 1), q);
    }

    @Test
    void randomTest() throws DataValidationException {
        List<Question> questions = IntStream.range(1, 10)
                .mapToObj(i -> new Question(i, "Test" + i))
                .collect(Collectors.toList());


        AnswerDao answerDao = mock(AnswerDao.class);
        QuestionDao questionDao = mock(QuestionDao.class);

        when(questionDao.getCount()).thenReturn(questions.size());
        when(questionDao.getById(anyInt())).thenAnswer(i -> questions.get((Integer)i.getArgument(0)-1));

        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);

        List<Question> randomQuestions = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Question question = service.getRandom();
            String q = question.getQuestion();
            randomQuestions.add(question);
            assertEquals(Integer.parseInt(q.substring(4)), question.getId());
        }
      //  questions.add(new Question(20,"Wrong"));
        assertTrue(randomQuestions.containsAll(questions));

    }

    @Test
    void getAnswers() throws DataValidationException {
        List<Question> questions = IntStream.range(1, 10)
                .mapToObj(i -> new Question(i, "Test" + i))
                .collect(Collectors.toList());

        List<Answer> answers = questions.stream()
                .map(Question::getId)
                .map(id -> Stream.of(new Answer(id * 3, id, true, "ans1"),
                        new Answer(id * 3 + 1, id, false, "ans2"),
                        new Answer(id * 3 + 2, id, false, "ans3")))
                .flatMap(Function.identity())
                .collect(Collectors.toList());


        QuestionDao questionDao = mock(QuestionDao.class);
        when(questionDao.getCount()).thenReturn(questions.size());
        when(questionDao.getById(anyInt())).thenAnswer(i -> questions.get(i.getArgument(0)));

        AnswerDao answerDao = mock(AnswerDao.class);
        when(answerDao.getByQuestionId(anyInt())).thenAnswer(i-> answers.stream()
                    .filter(a->a.getQuestionId()==(Integer) i.getArgument(0))
                    .collect(Collectors.toList()));


        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);

        for (int i = 0; i < 100 ; i++) {
            Random random= new Random();

            int id=random.nextInt(9)+1;
            List<Answer> ans = service.getAnswers(id);
            assertIterableEquals(ans,answers.stream()
                    .filter(a->a.getQuestionId()==id)
                    .collect(Collectors.toList()));
       //     System.out.println(ans);
        }


    }

    @Test
    void getCorrectAnswer() throws DataValidationException {
        List<Question> questions = IntStream.range(1, 10)
                .mapToObj(i -> new Question(i, "Test" + i))
                .collect(Collectors.toList());

        List<Answer> answers = questions.stream()
                .map(Question::getId)
                .map(id -> Stream.of(new Answer(id * 3, id, true, "ans1"),
                        new Answer(id * 3 + 1, id, false, "ans2"),
                        new Answer(id * 3 + 2, id, false, "ans3")))
                .flatMap(Function.identity())
                .collect(Collectors.toList());


        QuestionDao questionDao = mock(QuestionDao.class);
        when(questionDao.getCount()).thenReturn(questions.size());
        when(questionDao.getById(anyInt())).thenAnswer(i -> questions.get(i.getArgument(0)));

        AnswerDao answerDao = mock(AnswerDao.class);
        when(answerDao.getByQuestionId(anyInt())).thenAnswer(i-> answers.stream()
                .filter(a->a.getQuestionId()==(Integer) i.getArgument(0))
                .collect(Collectors.toList()));


        QuestionService service = new QuestionServiceImpl(questionDao, answerDao);

        for (int i = 0; i < 100 ; i++) {
            Random random= new Random();

            int id=random.nextInt(9)+1;
           Answer ans = service.getCorrectAnswer(id);
            assertEquals(ans,new Answer(id * 3, id, true, "ans1"));
        }
    }
}