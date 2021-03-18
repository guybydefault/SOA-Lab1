package ru.guybydefault.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.guybydefault.LocalDateTimeAdapter;
import ru.guybydefault.domain.Furnish;
import ru.guybydefault.domain.Transport;
import ru.guybydefault.domain.View;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatDto implements Serializable {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Min(0)
    private int id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotBlank
    @NotNull
    private String name; //Поле не может быть null, Строка не может быть пустой

    @Embedded
    @NotNull
    @Valid
    private CoordinatesDto coordinates; //Поле не может быть null

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @Min(0)
    private Float area; //Значение поля должно быть больше 0

    @Min(0)
    @Max(8)
    private Long numberOfRooms; //Максимальное значение поля: 8, Значение поля должно быть больше 0

    private Furnish furnish; //Поле может быть null

    @NotNull
    private View view; //Поле не может быть null

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Transport transport; //Поле не может быть null

    @Embedded
    @NotNull
    @Valid
    private HouseDto house; //Поле не может быть null

    @PrePersist
    protected void onCreate() {
        creationDate = LocalDateTime.now();
    }

    @JsonIgnore
    public boolean isNew() {
        return id == 0;
    }

    @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
    @XmlElement(name = "creationDate", required = true)
    public LocalDateTime getCreationDate() {
        return creationDate;
    }


}