package ru.otus.hw1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.otus.hw1.services.DataValidator;
import ru.otus.hw1.services.Quiz;
import ru.otus.hw1.utils.DataValidationException;

@Slf4j
@SpringBootApplication
public class Main {


    public static void main(String[] args) {

        log.info("file encodind before : {}", System.getProperty("file.encoding"));
        System.setProperty("file.encoding", "UTF-8");
        log.info("file encodind after: {} ", System.getProperty("file.encoding"));

        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        DataValidator validator = context.getBean(DataValidator.class);
        try {
            log.info("Start Data validation");
            validator.validate();
        } catch (DataValidationException e) {
            System.out.println("Целостность данных нарушена. Запуск программы не возможен.");
            log.error("Data validation Error! Stop program.", e);
            System.exit(0);
        }

        Quiz quiz = context.getBean(Quiz.class);
        quiz.initQuiz(System.in, System.out);

    }
}
