package ru.otus.hw1.model;

import com.univocity.parsers.annotations.BooleanString;
import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    @Parsed
    private int Id;
    @Parsed
    private int questionId;
    @Parsed
    @BooleanString(trueStrings = "t",falseStrings = "f")
    private boolean isCorrect;
    @Parsed
    private String Answer;
}
