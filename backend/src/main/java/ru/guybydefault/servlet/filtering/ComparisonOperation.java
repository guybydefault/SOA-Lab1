package ru.guybydefault.servlet.filtering;

public enum ComparisonOperation {
    EQUAL("="),
    LT("<"),
    GT(">"),
    GTE(">="),
    LTE("<=");

    String name;

    ComparisonOperation(String name) {
        this.name = name;
    }
}
