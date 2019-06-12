package ru.otus.hw1;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw1.services.DataValidator;
import ru.otus.hw1.services.Quiz;
import ru.otus.hw1.utils.DataValidationException;

import java.util.Locale;
import java.util.Properties;

@Slf4j
public class Main {


    public static void main(String[] args) {
        log.info("file encodind before : {}",System.getProperty("file.encoding"));
        System.setProperty("file.encoding", "UTF-8");
        log.info("file encodind after: {} ",System.getProperty("file.encoding"));


        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Config.class);
        DataValidator validator = context.getBean(DataValidator.class);
        try {
            log.info("Start Data validation");
            validator.validate();
        } catch (DataValidationException e) {
            System.out.println("Целостность данных нарушена. Запуск программы не возможен.");
            log.error("Data validation Error! Stop program.",e);
            System.exit(0);
        }

        Quiz quiz = context.getBean(Quiz.class);
        quiz.initQuiz(System.in,System.out);

    }
}
