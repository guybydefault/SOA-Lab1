package ru.guybydefault.lab2.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class Coordinates {
    private int x;

    private Float y;

    @JsonIgnore
    private Double distance = null;
}

