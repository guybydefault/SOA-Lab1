package ru.guybydefault.web.filtering;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchCriteria {
    private String[] keyPath;
    private ComparisonOperation operation;
    private String value;
}
