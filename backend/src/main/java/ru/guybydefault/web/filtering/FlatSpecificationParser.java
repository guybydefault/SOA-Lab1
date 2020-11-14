package ru.guybydefault.web.filtering;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlatSpecificationParser {
    public static FlatSpecification parse(List<List<String>> criteria) {
        if (criteria == null) {
            criteria = new ArrayList<>();
        }

        if (criteria.stream().anyMatch(x -> x.size() != 3)) {
            return null;
        }

        try {
            List<SearchCriteria> searchCriteria = criteria.stream()
                    .map(x ->
                            SearchCriteria.builder()
                                    .keyPath(x.get(0).split("\\."))
                                    .operation(ComparisonOperation.valueOf(x.get(1)))
                                    .value(x.get(2))
                                    .build()
                    )
                    .collect(Collectors.toList());
            return new FlatSpecification(searchCriteria);
        } catch (Exception e) {
            return null;
        }
    }
}
