package ru.otus.hw1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw1.services.Quiz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application_en.properties")
@SpringBootTest
public class QuizEnImplTest {


    private final String input="Name \n"+
            "Surname \n"+"1 \n"+"1 \n"+"1 \n"+"1 \n"+"4 \n"+"0 \n" + "text \n"+"1 \n";

    @Autowired
    private Quiz quiz;


    @Autowired
    private MessageSource messageSource;

    private final Locale locale = new Locale("en");

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
