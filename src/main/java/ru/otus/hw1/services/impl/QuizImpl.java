package ru.otus.hw1.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.model.User;
import ru.otus.hw1.services.QuestionService;
import ru.otus.hw1.services.Quiz;
import ru.otus.hw1.utils.DataValidationException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class QuizImpl implements Quiz {
    private final QuestionService questionService;
    private Scanner sc;
    private PrintStream out;

    public QuizImpl(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Override
    public void initQuiz(InputStream in, OutputStream out) {
        this.sc = new Scanner(in, StandardCharsets.UTF_8);
        this.out = new PrintStream(out);

        User user = getUser();
        startQuiz(user);
        printResult(user);
    }

    private User getUser() {
        out.print("Введите ваше имя:\n");
        String name = sc.next();
        out.print("Введите вашe фамилию:\n");
        String surname = sc.next();
        return new User(name, surname);
    }

    private User startQuiz(User user) {
        Map<Question, Answer> userAnswers = user.getAnswers();
        for (int i = 0; i < 5; i++) {
            out.println();
            Question question = getRandom(user);
            out.println(question.getQuestion());
            List<Answer> answers = questionService.getAnswers(question.getId());
            Map<Integer, Answer> answerMap = new HashMap<>();
            int j = 1;
            for (Answer ans : answers) {
                answerMap.put(j, ans);
                out.println(j + " " + ans.getAnswer());
                j++;
            }
            int rez = inputAnswer(j);
            userAnswers.put(question, answerMap.get(rez));
        }
        return user;
    }

    private Question getRandom(User user) {
        Question question;
        try {
            question= questionService.getRandom();
        } catch (DataValidationException e) {
            log.warn(e.getMessage());
            question= getRandom(user);
        }
        if (user.getAnswers().containsKey(question)) question=getRandom(user);
        return question;
    }

    private int inputAnswer(int max) {
        out.print("Введите ответ:\n");
 //       Scanner scan = new Scanner(in);
        try {
            int rez = sc.nextInt();
            if (rez > 0 && rez < 4) return rez;
            else throw new InputMismatchException();
        } catch (InputMismatchException e) {
            out.println("Необходимо ввести число от 1 до " + (max - 1));
            return inputAnswer(max);
        }
    }


    private void printResult(User user) {
        List<Answer> correctList = user.getAnswers()
                .values()
                .stream()
                .filter(Answer::isCorrect)
                .collect(Collectors.toList());
        int rez = correctList.size();
        String str = String.format("%s ваша оценка %d", user.getName(), rez);
        out.println(str);
        if (rez < 5) {
            out.println("Вы не правильно ответили на вопросы:");
            user.getAnswers().entrySet().stream()
                    .filter(e -> !e.getValue().isCorrect())
                    .forEach(e -> {
                        String q = e.getKey().getQuestion();
                        String correct = questionService.getCorrectAnswer(e.getKey().getId())
                                .getAnswer();
                        String ans = e.getValue().getAnswer();
                        out.println("==================================");
                        out.println(q);
                        out.println("Правильный ответ: " + correct);
                        out.println("Ваш ответ: " + ans);

                    });
        }
    }
}
