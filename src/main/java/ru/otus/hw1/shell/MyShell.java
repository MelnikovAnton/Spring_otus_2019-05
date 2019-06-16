package ru.otus.hw1.shell;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.shell.Availability;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.shell.standard.*;
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;
import ru.otus.hw1.config.ApplicationSettings;
import ru.otus.hw1.config.Config;
import ru.otus.hw1.model.User;
import ru.otus.hw1.services.QuestionService;
import ru.otus.hw1.services.Quiz;
import ru.otus.hw1.utils.DataValidationException;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Locale;

@ShellComponent(value = "Quiz Commands")
@RequiredArgsConstructor
@ShellCommandGroup("Quiz")
@Slf4j
public class MyShell implements PromptProvider {


    private final MessageSource messageSource;
    private final QuestionService questionService;
    private final ApplicationSettings settings;
    private final Quiz quiz;

    private User user;

    @Override
    public AttributedString getPrompt() {
        return (user != null) ? new AttributedString(user.getName() + user.getSurname() + ":>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
                : new AttributedString("unknown:>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
    }

    @PostConstruct
    public void init() {
        quiz.initQuiz(System.in, System.out);
    }


    @ShellMethod(value = "Start Quiz.", key = "start")
    @ShellMethodAvailability("isLogged")
    public String startQuiz() {
        user.getAnswers().clear();
        quiz.startQuiz(user);
        int result = quiz.getResult(user);


        String score = messageSource.getMessage("output.score"
                , new String[]{user.getName(), String.valueOf(result)}
                , Locale.getDefault());


        String isPassed = (result > settings.getRequired()) ?
                messageSource.getMessage("output.passed"
                        , null
                        , Locale.getDefault())
                : messageSource.getMessage("output.failed"
                , null
                , Locale.getDefault());

        return score + "\n" + isPassed;
    }

    @ShellMethod(value = "Print list result.", key = "pr", group = "print")
    @ShellMethodAvailability({"isQuized"})
    public void printRezult() {
        quiz.printResult(user);
    }

    @ShellMethod(value = "Print table result.", key = "pt", group = "print")
    @ShellMethodAvailability({"isQuized"})
    private Table printAnswers() {

        TableModelBuilder<Object> modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow()
                .addValue(messageSource.getMessage("table.question"
                        , null
                        , Locale.getDefault()))
                .addValue(messageSource.getMessage("table.correct"
                        , null
                        , Locale.getDefault()))
                .addValue(messageSource.getMessage("table.wrong"
                        , null
                        , Locale.getDefault()));
        user.getAnswers().entrySet().stream()
                .filter(e -> !e.getValue().isCorrect())
                .forEach(e -> {
                            modelBuilder.addRow()
                                    .addValue(e.getKey().getQuestion())
                                    .addValue(questionService.getCorrectAnswer(e.getKey().getId()).getAnswer())
                                    .addValue(e.getValue().getAnswer());
                        }
                );

        TableBuilder builder = new TableBuilder(modelBuilder.build());

        return builder.addFullBorder(BorderStyle.fancy_double).build();
    }

    private Availability isLogged() {
        return user == null ? Availability.unavailable("Сначала залогиньтесь") : Availability.available();
    }

    private Availability isQuized() {
        return user == null || user.getAnswers().size() != 5 ?
                Availability.unavailable("Сначала пройдите тест.") :
                Availability.available();
    }

    @ShellMethod(value = "Login method.", key = {"login", "l"})
    public String login(@ShellOption(value = {"-n", "--name", ""}) String name,
                        @ShellOption(value = {"-s", "--surname"}, defaultValue = "") String surname) {
        this.user = new User(name, surname);
        return messageSource.getMessage("hello"
                , new String[]{user.getName(), user.getSurname()}
                , Locale.getDefault());
    }


    @ShellMethod(value = "Change locale. Supported Options is [ru, en]", key = {"locale", "cl"})
    public String changeLocale(@ShellOption(defaultValue = "ru") String locale,
                               @ShellOption(value = {"-v", "--view"}, defaultValue = "False") boolean view) {
        if (view) return messageSource.getMessage("current.locale"
                , new String[]{Locale.getDefault().getDisplayName()}
                , Locale.getDefault());

        Locale.setDefault(Locale.forLanguageTag(locale));
        String question;
        String answer;
        String error = "";
        try {
            question = checkLocalized(settings.getQuestion(), locale);
        } catch (DataValidationException e) {
            question = settings.getQuestion();
            error = messageSource.getMessage("localized.question.error"
                    , new String[]{Locale.getDefault().getDisplayName()}
                    , Locale.getDefault());
        }
        Resource questionResource = new ClassPathResource(question);
        questionService.reloadQuestions(questionResource);

        try {
            answer = checkLocalized(settings.getAnswer(), locale);
        } catch (DataValidationException e) {
            answer = settings.getAnswer();
            error = error + "\n" + messageSource.getMessage("localized.answer.error"
                    , new String[]{Locale.getDefault().getDisplayName()}
                    , Locale.getDefault());
        }

        Resource answerResource = new ClassPathResource(answer);
        questionService.reloadAnswer(answerResource);

        String out = messageSource.getMessage("locale.changed"
                , new String[]{Locale.getDefault().getDisplayName()}
                , Locale.getDefault());

        return out + "\n" + error;
    }

    private String checkLocalized(String resourceName, String locale) throws DataValidationException {
        String localized = resourceName.replace(".csv", "_" + locale + ".csv");
        URL u = Config.class.getResource("/" + localized);
        if (u == null) {
            String msg = String.format("No localized resources: %s. Return default %s", localized, resourceName);
            log.warn(msg);
            throw new DataValidationException(msg);
        } else return localized;
    }
}





