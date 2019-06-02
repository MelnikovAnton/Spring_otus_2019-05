package ru.otus.hw1;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw1.services.DataValidator;
import ru.otus.hw1.services.Quiz;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuizImplTest {


    @Test
    void quizWithSpringContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/quiz-context.xml");
        Quiz quiz = (Quiz) context.getBean("quizService");
        String input="Name \n"+
                "Sername \n"+"1 \n"+"1 \n"+"1 \n"+"1 \n"+"4 \n"+"0 \n"+"1 \n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        OutputStream out = new ByteArrayOutputStream();
        quiz.initQuiz(in,out);
        String result = out.toString();
        //System.out.println(result);

        assertTrue(result.contains("Name ваша оценка"));
        assertTrue(result.contains("Необходимо ввести число от 1 до 3"));

        List<String> lines = Arrays.asList(result.split("\n"));
        for (String line:lines) {
            if (line.startsWith("Ваш ответ: ")) {
               assertTrue(lines.contains("1 "+line.substring(10).trim()));
            }
        }

    }

}