package ru.guybydefault.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class House  {

    @NotNull
    @Column(name = "house_name")
    private String name; //Поле не может быть null

    @Min(0)
    @Column(name = "house_year")
    private int year; //Значение поля должно быть больше 0

    @Min(0)
    @Column(name = "house_number_of_floors")
    private long numberOfFloors; //Значение поля должно быть больше 0

    @Min(0)
    @Column(name = "house_number_of_flats_on_floor")
    private Integer numberOfFlatsOnFloor; //Значение поля должно быть больше 0

    @Min(0)
    @Column(name = "house_number_of_lifts")
    private int numberOfLifts; //Значение поля должно быть больше 0
}
