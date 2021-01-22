package ru.guybydefault.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.guybydefault.web.sort.Sort;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class PageRequest implements Serializable {
    private int page;
    private int size;
    private Sort sort;
}
