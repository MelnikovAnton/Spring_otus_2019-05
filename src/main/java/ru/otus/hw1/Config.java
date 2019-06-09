package ru.otus.hw1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.otus.hw1.services.QuestionService;
import ru.otus.hw1.services.Quiz;
import ru.otus.hw1.services.impl.QuizImpl;

import java.net.URL;
import java.util.Locale;

@Configuration
@ComponentScan
@PropertySource("classpath:/application.properties")
@Slf4j
public class Config {

    @Value("${locale}")
    private String locale;
    /*
     * Нужен для того чтобы проинжектелась locale
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer configurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean("question")
    Resource questionsResource(@Value("${question}") String value) {
      return new ClassPathResource(checkLocalized(value));
    }

    @Bean("answer")
    Resource answerResource(@Value("${answer}") String value) {
        return new ClassPathResource(checkLocalized(value));
    }


    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        Locale.setDefault(new Locale(locale));
        return messageSource;
    }

    @Bean
    Quiz getQuiz(QuestionService questionService,
                 MessageSource messageSource,
                 @Value("${required}") int required){
        return new QuizImpl(questionService,messageSource,required);
    }

    private String checkLocalized(String resourceName){
        String localized = resourceName.replace(".csv", "_" + locale + ".csv");
        URL u = Config.class.getResource("/"+localized);
        if (u==null) {
            log.warn("No localized resources: {}. Return default {}",localized,resourceName);
            return resourceName;
        } else return localized;
    }

}
