package com.vida.klas.dto;

import com.vida.student.dto.StudentDto;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

public class KlasDto {
    private Long id;
    @NotNull(message = "Klas number is mandatory")
    @Positive(message = "Klas number can not be zero or negative")
    @Max(value = 12, message = "Max klas number should be 12")
    private Long klasNumber;
    @NotNull(message = "Klas letter is mandatory")
    @Size(min = 1, max = 1, message = "Klas letter should be 1 symbol!")
    @Pattern(regexp = "[ABCD]", message = "Klas letter should be only A, B, C or D")
    private String klasLetter;

    private List<StudentDto> students;

    public KlasDto() {
    }

    public KlasDto(Long id, Long klasNumber, String klasLetter) {
        this.klasNumber = klasNumber;
        this.klasLetter = klasLetter;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public KlasDto setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getKlasNumber() {
        return klasNumber;
    }

    public KlasDto setKlasNumber(Long klasNumber) {
        this.klasNumber = klasNumber;
        return this;
    }

    public String getKlasLetter() {
        return klasLetter;
    }

    public KlasDto setKlasLetter(String klasLetter) {
        this.klasLetter = klasLetter;
        return this;
    }

    public List<StudentDto> getStudents() {
        return students;
    }

    public KlasDto setStudents(List<StudentDto> students) {
        this.students = students;
        return this;
    }

    @Override
    public String toString() {
        return "KlasDto{" +
                "klasNumber=" + klasNumber +
                ", klasLetter='" + klasLetter + '\'' +
                '}';
    }
}
