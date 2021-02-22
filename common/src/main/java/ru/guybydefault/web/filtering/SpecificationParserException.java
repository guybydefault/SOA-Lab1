package ru.guybydefault.web.filtering;

import java.io.Serializable;

public class SpecificationParserException extends RuntimeException implements Serializable {
    public SpecificationParserException(Exception cause) {
        super(cause);
    };
}
