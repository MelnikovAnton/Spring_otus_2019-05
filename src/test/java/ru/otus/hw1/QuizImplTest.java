package ru.otus.hw1;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw1.services.Quiz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:/application_en.properties")
@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false"})
@ActiveProfiles(profiles = "shell")
public class QuizImplTest {


    private final String inputEN = "en \n " +
            "Name \n" +
            "Surname \n" + "1 \n" + "1 \n" + "1 \n" + "1 \n" + "4 \n" + "0 \n" + "text \n" + "1 \n";

    private final String inputRU = "ru \n " +
            "Name \n" +
            "Surname \n" + "1 \n" + "1 \n" + "1 \n" + "1 \n" + "4 \n" + "0 \n" + "text \n" + "1 \n";

    @Autowired
    private Quiz quiz;
    @Autowired
    private MessageSource messageSource;


    @TestFactory
    List<DynamicTest> switchLangTest() {

        DynamicTest test1 = DynamicTest.dynamicTest("EN Test", () -> {
            ByteArrayInputStream in = new ByteArrayInputStream(inputEN.getBytes());
            OutputStream out = new ByteArrayOutputStream();
            assertDoesNotThrow(() -> quiz.initQuiz(in, out));
            assertDoesNotThrow(() -> quiz.startQuiz());
            String result = out.toString();
            assertTrue(
                    result.contains(messageSource.getMessage("input.wrong",
                            new Integer[]{3},
                            Locale.forLanguageTag("en")))
            );
        });

        DynamicTest test2 = DynamicTest.dynamicTest("RU Test", () -> {
            ByteArrayInputStream in = new ByteArrayInputStream(inputRU.getBytes());
            OutputStream out = new ByteArrayOutputStream();
            assertDoesNotThrow(() -> quiz.initQuiz(in, out));
            assertDoesNotThrow(() -> quiz.startQuiz());
            String result = out.toString();
            assertTrue(
                    result.contains(messageSource.getMessage("input.wrong",
                            new Integer[]{3},
                            Locale.forLanguageTag("ru")))
            );

        });

        DynamicTest test3 = DynamicTest.dynamicTest("Default(RU) Test", () -> {
            ByteArrayInputStream in = new ByteArrayInputStream(inputRU.getBytes());
            OutputStream out = new ByteArrayOutputStream();
            assertDoesNotThrow(() -> quiz.initQuiz(in, out));
            assertDoesNotThrow(() -> quiz.startQuiz());
            String result = out.toString();
            assertTrue(
                    result.contains(messageSource.getMessage("input.wrong",
                            new Integer[]{3},
                            Locale.forLanguageTag("ru")))
            );

        });
        return Arrays.asList(test1, test2, test3);
    }
}