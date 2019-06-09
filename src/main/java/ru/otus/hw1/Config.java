package ru.otus.hw1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan
@PropertySource("classpath:/application.properties")
public class Config {

    @Bean("question")
    Resource questionsResource(@Value("${question}") String value){
        System.out.println(value);
        return new ClassPathResource(value);
    }
    @Bean("answer")
    Resource answerResource(@Value("${answer}") String value){
        System.out.println(value);
        return new ClassPathResource(value);
    }

}
