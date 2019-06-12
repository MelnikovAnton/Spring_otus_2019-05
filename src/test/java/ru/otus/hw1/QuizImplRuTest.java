package ru.otus.hw1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import ru.otus.hw1.services.DataValidator;
import ru.otus.hw1.services.Quiz;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = Config.class)
@TestPropertySource("classpath:/application_ru.properties")
class QuizImplRuTest {

    private final String input="Name \n"+
            "Surname \n"+"1 \n"+"1 \n"+"1 \n"+"1 \n"+"4 \n"+"0 \n" + "text \n"+"1 \n";

    @Autowired
    private Quiz quiz;


    @Autowired
    private MessageSource messageSource;

    private final Locale locale = new Locale("ru");

    @Test
    void quizWithSpringContext() {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        OutputStream out = new ByteArrayOutputStream();
        assertDoesNotThrow(()->quiz.initQuiz(in,out));
        String result = out.toString();
//        System.out.println(result);
//        System.out.println(messageSource.getMessage("input.wrong",new Integer[]{3},locale));

        assertTrue(result.contains(messageSource.getMessage("input.wrong",new Integer[]{3},locale )));

    }

}