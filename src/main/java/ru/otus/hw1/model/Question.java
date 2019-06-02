package ru.otus.hw1.model;

import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {
    @Parsed
    private int id;
    @Parsed
    private String question;
}
