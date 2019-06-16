package ru.otus.hw1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class Main {

    public static void main(String[] args) {

        log.info("file encodind before : {}", System.getProperty("file.encoding"));
        System.setProperty("file.encoding", "UTF-8");
        log.info("file encodind after: {} ", System.getProperty("file.encoding"));

        ConfigurableApplicationContext ctx = SpringApplication.run(Main.class, args);
    }
}
