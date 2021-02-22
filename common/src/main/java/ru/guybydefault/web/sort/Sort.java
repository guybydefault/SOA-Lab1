package ru.guybydefault.web.sort;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.guybydefault.web.filtering.FlatSpecification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Sort implements Serializable {
    private List<SortOrder> sortOrderList;

    public List<Order> buildOrder(CriteriaBuilder criteriaBuilder, Root root) {
        return (List<Order>) getSortOrderList().stream().map(
                sortOrder -> {
                    if (sortOrder.getSortDirection() == SortDirection.ASC) {
                        return criteriaBuilder.asc(FlatSpecification.getPath(root, sortOrder.getKeyPath()));
                    } else {
                        return criteriaBuilder.desc(FlatSpecification.getPath(root, sortOrder.getKeyPath()));
                    }
                }).collect(Collectors.toList());
    }
}
