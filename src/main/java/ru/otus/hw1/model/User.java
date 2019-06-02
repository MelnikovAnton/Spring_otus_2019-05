package ru.otus.hw1.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    private final String name;
    private final String surname;
    private final Map<Question,Answer> answers=new HashMap<>();
}
