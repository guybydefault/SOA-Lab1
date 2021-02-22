package ru.guybydefault.lab2.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

@Data

public class Flat {

    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически


    private String name; //Поле не может быть null, Строка не может быть пустой


    private Coordinates coordinates; //Поле не может быть null

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    private Float area; //Значение поля должно быть больше 0

    private Long numberOfRooms; //Максимальное значение поля: 8, Значение поля должно быть больше 0

    private Furnish furnish; //Поле может быть null

    private View view; //Поле не может быть null

    private Transport transport; //Поле не может быть null

    private House house; //Поле не может быть null
}