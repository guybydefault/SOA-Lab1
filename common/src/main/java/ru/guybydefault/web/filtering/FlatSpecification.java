package ru.guybydefault.web.filtering;

import ru.guybydefault.domain.Furnish;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.domain.View;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FlatSpecification implements Serializable {
    List<SearchCriteria> searchCriteriaList;

    public FlatSpecification(List<SearchCriteria> searchCriteriaList) {
        this.searchCriteriaList = searchCriteriaList;
    }

    public static <T> Path<T> getPath(Root root, String[] key) {
        Path<Object> path = root.get(key[0]);

        for (int i = 1; i < key.length; i++) {
            path = path.get(key[i]);
        }

        return (Path<T>) path;
    }

    public Predicate toPredicate(Root root, CriteriaQuery<?> _query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : searchCriteriaList) {
            try {
                final Class<?> javaType = getPath(root, criteria.getKeyPath()).getJavaType();

                if (javaType.equals(Transport.class)) {
                    add(builder, predicates, criteria, getPath(root, criteria.getKeyPath()), Transport.valueOf(criteria.getValue()));
                } else if (javaType.equals(Furnish.class)) {
                    add(builder, predicates, criteria, getPath(root, criteria.getKeyPath()), Furnish.valueOf(criteria.getValue()));
                } else if (javaType.equals(View.class)) {
                    add(builder, predicates, criteria, getPath(root, criteria.getKeyPath()), View.valueOf(criteria.getValue()));
                } else if (javaType.equals(LocalDate.class)) {
                    add(builder, predicates, criteria, getPath(root, criteria.getKeyPath()), LocalDate.parse(criteria.getValue()));
                } else {
                    if (javaType.equals(Integer.class)) {
                        Integer.parseInt(criteria.getValue());
                    } else if (javaType.equals(Double.class)) {
                        Double.parseDouble(criteria.getValue());
                    }
                    add(builder, predicates, criteria, getPath(root, criteria.getKeyPath()), criteria.getValue());
                }
            } catch (IllegalArgumentException | DateTimeParseException e) {
                throw new SpecificationParserException(e);
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }

    private <Y extends Comparable<? super Y>> void add(CriteriaBuilder builder, List<Predicate> predicates, SearchCriteria criteria, Path<Y> path, Y value) {
        switch (criteria.getOperation()) {
            case GT:
                predicates.add(builder.greaterThan(path, value));
                break;
            case LT:
                predicates.add(builder.lessThan(path, value));
                break;
            case GTE:
                predicates.add(builder.greaterThanOrEqualTo(path, value));
                break;
            case LTE:
                predicates.add(builder.lessThanOrEqualTo(path, value));
                break;
            case EQUAL:
                predicates.add(builder.equal(path, value));
                break;
        }
    }
}