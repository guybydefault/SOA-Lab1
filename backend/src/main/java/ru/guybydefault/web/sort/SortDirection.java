package ru.guybydefault.web.sort;

public enum SortDirection {
    ASC("ASC"),
    DESC("DESC");

    private String name;

    SortDirection(String name) {
        this.name = name;
    }

}
