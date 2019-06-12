package ru.otus.hw1.utils;

import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public class CsvReader {

    @SuppressWarnings("deprecation")
    public static <T> List<T> readData(Resource resource, Class<T> clazz ){
        BeanListProcessor<T> rowProcessor = new BeanListProcessor<T>(clazz);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setRowProcessor(rowProcessor);
        parserSettings.setHeaderExtractionEnabled(true);

        CsvParser parser = new CsvParser(parserSettings);

        try {
            parser.parse(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<T> elements = rowProcessor.getBeans();

        return elements;
    }
}
