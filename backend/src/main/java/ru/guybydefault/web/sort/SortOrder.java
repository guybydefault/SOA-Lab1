package ru.guybydefault.web.sort;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SortOrder {
    private String[] keyPath;
    private SortDirection sortDirection;
}
