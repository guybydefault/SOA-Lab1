package ru.guybydefault.web.sort;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SortOrder implements Serializable {
    private String[] keyPath;
    private SortDirection sortDirection;
}
