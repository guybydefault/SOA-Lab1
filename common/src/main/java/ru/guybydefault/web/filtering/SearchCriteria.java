package ru.guybydefault.web.filtering;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class SearchCriteria implements Serializable  {
    private String[] keyPath;
    private ComparisonOperation operation;
    private String value;
}
