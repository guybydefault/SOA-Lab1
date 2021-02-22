package ru.guybydefault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatesDto implements Serializable {
    private int x;

    @NotNull
    private Float y; //Поле не может быть null
}

