package ru.otus.hw1;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.shell.CommandNotCurrentlyAvailable;
import org.springframework.shell.ParameterMissingResolutionException;
import org.springframework.shell.Shell;
import org.springframework.shell.jline.InteractiveShellApplicationRunner;
import org.springframework.shell.table.Table;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw1.services.Quiz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {
        InteractiveShellApplicationRunner.SPRING_SHELL_INTERACTIVE_ENABLED + "=false",
        "spring.profiles.active=shell"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MyShellTest {

    private final String inputEN = "en \n " +
            "Name \n" +
            "Surname \n" + "1 \n" + "1 \n" + "1 \n" + "1 \n" + "4 \n" + "0 \n" + "text \n" + "1 \n";

    @Autowired
    private Shell shell;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private Quiz quiz;

    @Test
    @DisplayName("Все команды есть.")
    void testAllCommandsExists() {
        Set<String> keys = shell.listCommands().keySet();
        Assertions.assertTrue(keys.containsAll(Arrays.asList("cl", "l", "locale", "login", "pr", "pt", "start")));
    }

    @TestFactory
    @DisplayName("Проверка локали.")
    List<DynamicTest> testLocalale() {

        DynamicTest clDefault = DynamicTest.dynamicTest("Локаль по умолчанию", () -> {
            String r = (String) shell.evaluate(() -> "cl");
            assertTrue(r.contains("русский"));
        });

        DynamicTest localeView = DynamicTest.dynamicTest("Текущая локаль", () -> {
            String r = (String) shell.evaluate(() -> "locale -v");
            assertEquals(messageSource.getMessage("current.locale"
                    , new String[]{Locale.getDefault().getDisplayName()}
                    , Locale.getDefault()), r);
        });

        DynamicTest localeEN = DynamicTest.dynamicTest("Переключение на EN", () -> {
            String r = (String) shell.evaluate(() -> "locale en");
            assertTrue(r.contains("English"));
        });

        DynamicTest localeUnknown = DynamicTest.dynamicTest("Неизвестная локаль", () -> {
            String r = (String) shell.evaluate(() -> "locale am");
            assertTrue(r.contains(messageSource.getMessage("localized.question.error"
                    , new String[]{Locale.getDefault().getDisplayName()}
                    , Locale.getDefault())));
            assertTrue(r.contains(messageSource.getMessage("localized.answer.error"
                    , new String[]{Locale.getDefault().getDisplayName()}
                    , Locale.getDefault())));
        });

        return Arrays.asList(localeUnknown, localeView, localeEN, clDefault);
    }

    @TestFactory
    @DisplayName("Проверка недоступности методов.")
    List<DynamicTest> testAvailability() {

        DynamicTest start = DynamicTest.dynamicTest("Start", () -> {
            CommandNotCurrentlyAvailable r = (CommandNotCurrentlyAvailable) shell.evaluate(() -> "start");
            assertFalse(r.getAvailability().isAvailable());
        });

        DynamicTest pr = DynamicTest.dynamicTest("pr", () -> {
            CommandNotCurrentlyAvailable r = (CommandNotCurrentlyAvailable) shell.evaluate(() -> "pr");
            assertFalse(r.getAvailability().isAvailable());
        });

        DynamicTest pt = DynamicTest.dynamicTest("pt", () -> {
            CommandNotCurrentlyAvailable r = (CommandNotCurrentlyAvailable) shell.evaluate(() -> "pt");
            assertFalse(r.getAvailability().isAvailable());
        });

        return Arrays.asList(start, pr, pt);
    }


    @TestFactory
    @DisplayName("Проверка логина.")
    List<DynamicTest> testLogin() {

        DynamicTest loginWrong = DynamicTest.dynamicTest("Логин без имени", () -> {
            ParameterMissingResolutionException r = (ParameterMissingResolutionException) shell.evaluate(() -> "l");
            //  System.out.println(r.getMessage());
            assertEquals("Parameter '-n string' should be specified", r.getMessage());
        });

        DynamicTest loginName = DynamicTest.dynamicTest("Логин по имени", () -> {
            String r = (String) shell.evaluate(() -> "login Test");
            assertEquals("Hello Test !", r);
        });

        DynamicTest loginNameSurname = DynamicTest.dynamicTest("Логин по имени", () -> {
            String r = (String) shell.evaluate(() -> "login -n Test -s Test");
            assertEquals("Hello Test Test!", r);
        });

        DynamicTest loginUnavaleblePR = DynamicTest.dynamicTest("Не доступна печать результата.", () -> {
            shell.evaluate(() -> "login -n Test -s Test");

            CommandNotCurrentlyAvailable pt = (CommandNotCurrentlyAvailable) shell.evaluate(() -> "pt");
            assertFalse(pt.getAvailability().isAvailable());

            CommandNotCurrentlyAvailable pr = (CommandNotCurrentlyAvailable) shell.evaluate(() -> "pr");
            assertFalse(pr.getAvailability().isAvailable());

        });

        return Arrays.asList(loginWrong, loginName, loginNameSurname, loginUnavaleblePR);
    }


    @TestFactory
    @DisplayName("Проверка логина и тестирования.")
    List<DynamicTest> testLoginAndQuiz() {


        DynamicTest loginAndStart = DynamicTest.dynamicTest("Логин и старт тестирования", () -> {
            initQuiz();
            shell.evaluate(() -> "l TestName");
            String r = assertDoesNotThrow(() -> (String) shell.evaluate(() -> "start"));
            assertTrue(r.contains("TestName"));
        });

        DynamicTest loginStartPr = DynamicTest.dynamicTest("Логин старт тестирования и Pr", () -> {
            OutputStream out = initQuiz();
            shell.evaluate(() -> "l TestName");
            shell.evaluate(() -> "start");
            shell.evaluate(() -> "pr");
            assertTrue(out.toString().contains("TestName"));
        });

        DynamicTest loginStartPt = DynamicTest.dynamicTest("Логин старт тестирования и Pt", () -> {
            initQuiz();

            shell.evaluate(() -> "l TestName");
            shell.evaluate(() -> "start");
            Table t = (Table) shell.evaluate(() -> "pt");
            String r = t.render(100);
            r = r.replaceAll("[^a-zA-ZА-Яа-я]+", " ");

            Set<String> resultWords = new HashSet<>(Arrays.asList(r.split(" ")));

            boolean result1 = resultWords.containsAll(Arrays.asList(
                    messageSource.getMessage("table.wrong"
                            , null
                            , Locale.getDefault()).split(" ")));
            boolean result2 = resultWords.containsAll(Arrays.asList(
                    messageSource.getMessage("table.question"
                            , null
                            , Locale.getDefault()).split(" ")));
            boolean result3 = resultWords.containsAll(Arrays.asList(
                    messageSource.getMessage("table.correct"
                            , null
                            , Locale.getDefault()).split(" ")));

            assertTrue(result1 && result2 && result3);
        });

        return Arrays.asList(loginAndStart, loginStartPr, loginStartPt);
    }

    private OutputStream initQuiz() {
        ByteArrayInputStream in = new ByteArrayInputStream(inputEN.getBytes());
        OutputStream out = new ByteArrayOutputStream();
        quiz.initQuiz(in, out);
        return out;
    }

}
