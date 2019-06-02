package ru.otus.hw1.utils;

import com.univocity.parsers.common.processor.BeanWriterProcessor;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ru.otus.hw1.model.Answer;
import ru.otus.hw1.model.Question;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class HtmlToCSV {

    private static AtomicInteger ansId = new AtomicInteger(1);

    public static void main(String[] args) throws Exception {

        Connection.Response response = Jsoup.connect("http://www.migrasib.ru/testirovanie/istoriya/").execute();
        Document html = response.parse();
        Elements questionsElements = html.getElementsByClass("test__vopros");
        List<Question> questions = new ArrayList<>();
        List<Answer> answers = new ArrayList<>();
        questionsElements.forEach(e -> {
            Question question = new Question();
            String q = e.select("h2").first().text();
            q=q.replaceAll("\\d+[.]","").trim();
            String id = e.attr("data-vopros");
            Elements ans = e.getElementsByClass("test__otvet");
            question.setQuestion(q);
            question.setId(Integer.parseInt(id));
            ans.forEach(a -> {
                Answer answer = new Answer();
                answer.setId(ansId.incrementAndGet());
        //        boolean correct = a.classNames().contains("test__otvet_correct");
                boolean correct = a.attr("data-otvet").equals("1");
                String answerStr = a.text();
                answer.setAnswer(answerStr);
                answer.setQuestionId(Integer.parseInt(id));
                answer.setCorrect(correct);
                answers.add(answer);
            });
            questions.add(question);
        });
        questions.forEach(System.out::println);
        System.out.println("=======================");
        answers.forEach(System.out::println);
        writeCSV(questions, "questions.csv", Question.class);
        writeCSV(answers, "answers.csv", Answer.class);

    }

    private static <T> void writeCSV(List<T> rows, String outFile, Class clazz) {
        CsvWriterSettings settings = new CsvWriterSettings();
        settings.setRowWriterProcessor(new BeanWriterProcessor<T>(clazz));
        File outputFile = Paths.get("src", "main", "resources", outFile).toFile();
        CsvWriter writer = new CsvWriter(outputFile, settings);
        writer.writeHeaders();
        writer.processRecords(rows);
        writer.flush();
        writer.close();


    }
}
