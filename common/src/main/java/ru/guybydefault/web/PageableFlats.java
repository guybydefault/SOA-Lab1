package ru.guybydefault.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.guybydefault.dto.FlatDto;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageableFlats implements Serializable {
    private FlatDto[] content;
    private long number;
    private long numberOfElements;
    private long size;
    private long totalElements;
    private long totalPages;
}
