package ru.otus.hw1.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.otus.hw1.config.ApplicationSettings;
import ru.otus.hw1.config.Config;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;
import ru.otus.hw1.model.User;
import ru.otus.hw1.services.QuestionService;
import ru.otus.hw1.services.Quiz;
import ru.otus.hw1.utils.DataValidationException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class QuizImpl implements Quiz {
    private final QuestionService questionService;
    private final MessageSource messageSource;
    private final int requiredAnswers;
    private final ApplicationSettings settings;
    private Scanner sc;
    private PrintStream out;

    public QuizImpl(QuestionService questionService, MessageSource messageSource, ApplicationSettings settings) {
        this.questionService = questionService;
        this.messageSource = messageSource;
        this.requiredAnswers = settings.getRequired();
        this.settings = settings;
    }

    @Override
    public void initQuiz(InputStream in, OutputStream out) {
        this.sc = new Scanner(in, StandardCharsets.UTF_8);
        this.out = new PrintStream(out);
    }

    @Override
    public void startQuiz() {
        if (sc == null || out == null) initQuiz(System.in, System.out);
        checkLocale();
        User user = getUser();
        startQuiz(user);
        printResult(user);
    }

    private void checkLocale() {
        out.println(messageSource.getMessage("input.selectLang", null, Locale.getDefault()));
        String lang = sc.next();
        changeLocale(lang);
    }

    private User getUser() {
        out.print(messageSource.getMessage("input.name", null, Locale.getDefault()));
        String name = sc.next();
        out.print(messageSource.getMessage("input.surname", null, Locale.getDefault()));
        String surname = sc.next();
        return new User(name, surname);
    }

    @Override
    public User startQuiz(User user) {
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
            question = questionService.getRandom();
        } catch (DataValidationException e) {
            log.warn(e.getMessage());
            question = getRandom(user);
        }
        if (user.getAnswers().containsKey(question)) question = getRandom(user);
        return question;
    }

    private int inputAnswer(int max) {
        out.print(messageSource.getMessage("input.answer", null, Locale.getDefault()));
        //       Scanner scan = new Scanner(in);
        try {
            int rez = sc.nextInt();
            if (rez > 0 && rez < max) return rez;
            else throw new InputMismatchException();
        } catch (InputMismatchException e) {
            out.println(messageSource.getMessage("input.wrong", new Integer[]{(max - 1)}, Locale.getDefault()));
            if (sc.hasNext()) sc.next();
            return inputAnswer(max);
        }
    }


    @Override
    public void printResult(User user) {
        int rez = getResult(user);
        String str = messageSource.getMessage("output.score", new String[]{user.getName(), String.valueOf(rez)}, Locale.getDefault());//String.format("%s ваша оценка %d", user.getName(), rez);
        out.println(str);
        if (rez >= requiredAnswers) {
            out.println(messageSource.getMessage("output.passed", null, Locale.getDefault()));
        } else {
            out.println(messageSource.getMessage("output.failed", null, Locale.getDefault()));
        }
        if (rez < 5) {
            out.println(messageSource.getMessage("output.answered.wrong", null, Locale.getDefault()));
            user.getAnswers().entrySet().stream()
                    .filter(e -> !e.getValue().isCorrect())
                    .forEach(e -> {
                        String q = e.getKey().getQuestion();
                        String correct = questionService.getCorrectAnswer(e.getKey().getId())
                                .getAnswer();
                        String ans = e.getValue().getAnswer();
                        out.println("==================================");
                        out.println(q);
                        out.println(messageSource.getMessage("output.correct.answer", new String[]{correct}, Locale.getDefault()));
                        out.println(messageSource.getMessage("output.correct.answer", new String[]{ans}, Locale.getDefault()));

                    });
        }
    }

    @Override
    public int getResult(User user) {
        List<Answer> correctList = user.getAnswers()
                .values()
                .stream()
                .filter(Answer::isCorrect)
                .collect(Collectors.toList());
        return correctList.size();
    }


    private void changeLocale(String locale) {
        Locale.setDefault(Locale.forLanguageTag(locale));
        Resource questionResource = new ClassPathResource(checkLocalized(settings.getQuestion(), locale));
        questionService.reloadQuestions(questionResource);

        Resource answerResource = new ClassPathResource(checkLocalized(settings.getAnswer(), locale));
        questionService.reloadAnswer(answerResource);
    }

    private String checkLocalized(String resourceName, String locale) {
        String localized = resourceName.replace(".csv", "_" + locale + ".csv");
        URL u = Config.class.getResource("/" + localized);
        if (u == null) {
            log.warn("No localized resources: {}. Return default {}", localized, resourceName);
            return resourceName;
        } else return localized;
    }
}
