package ru.guybydefault.web;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Pageable<T> implements Serializable {
    private Iterable<T> content;
    private long number;
    private long numberOfElements;
    private long size;
    private long totalElements;
    private long totalPages;
}
