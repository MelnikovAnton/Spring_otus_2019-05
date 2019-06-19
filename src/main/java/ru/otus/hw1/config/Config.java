package ru.otus.hw1.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import ru.otus.hw1.services.QuestionService;
import ru.otus.hw1.services.Quiz;
import ru.otus.hw1.services.impl.QuizConsoleImpl;
import ru.otus.hw1.services.impl.QuizImpl;

import java.net.URL;
import java.util.Locale;

@Configuration
@Slf4j
public class Config {

    private final ApplicationSettings settings;

    public Config(ApplicationSettings settings) {
        this.settings = settings;
    }


    /*
     * Нужен для того чтобы проинжектелась locale
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer configurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean("question")
    Resource questionsResource() {
        String value = settings.getQuestion();
        return new ClassPathResource(checkLocalized(value, settings.getLocale()));
    }

    @Bean("answer")
    Resource answerResource() {
        String value = settings.getAnswer();
        return new ClassPathResource(checkLocalized(value, settings.getLocale()));
    }


    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        String locale = settings.getLocale();
        Locale.setDefault(Locale.forLanguageTag(locale));
        return messageSource;
    }

    @Bean
    @Profile("shell")
    Quiz getShellQuiz(QuestionService questionService,
                      MessageSource messageSource) {
        return new QuizImpl(questionService, messageSource, settings);
    }

    @Bean(initMethod = "startQuiz")
    @Profile("console")
    Quiz getConsoleQuiz(QuestionService questionService,
                        MessageSource messageSource) {
        return new QuizConsoleImpl(questionService, messageSource, settings);
    }

    private String checkLocalized(String resourceName, String locale) {
        String localized = resourceName.replace(".csv", "_" + locale + ".csv");
        URL u = Config.class.getResource("/" + localized);
        if (u == null) {
            log.warn("No localized resources: {}. Return default {}", localized, resourceName);
            return resourceName;
        } else return localized;
    }

}
