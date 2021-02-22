package ru.guybydefault.lab2.domain;

import lombok.Data;

@Data
public class House {


    private String name; //Поле не может быть null


    private int year; //Значение поля должно быть больше 0


    private long numberOfFloors; //Значение поля должно быть больше 0


    private Integer numberOfFlatsOnFloor; //Значение поля должно быть больше 0

    private int numberOfLifts; //Значение поля должно быть больше 0
}
