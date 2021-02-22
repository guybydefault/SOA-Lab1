package ru.guybydefault.web.sort;

import java.io.Serializable;

public enum SortDirection implements Serializable {
    ASC("ASC"),
    DESC("DESC");

    private String name;

    SortDirection(String name) {
        this.name = name;
    }

}
