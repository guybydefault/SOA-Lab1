package ru.guybydefault.web.sort;

import java.io.Serializable;

public class SortParseOrderException extends RuntimeException implements Serializable {
    public SortParseOrderException(Exception cause) {
        super(cause);
    }
}
