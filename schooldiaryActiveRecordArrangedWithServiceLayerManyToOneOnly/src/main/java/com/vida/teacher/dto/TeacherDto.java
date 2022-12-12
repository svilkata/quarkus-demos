package com.vida.teacher.dto;

import com.vida.klas.dto.KlasDto;
import com.vida.subject.dto.SubjectDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

public class TeacherDto {
    private Long id;
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    @NotBlank(message = "EGN is mandatory")
    @Size(min = 10, max = 10, message = "EGN should be 10 symbols")
    private String egn;

    public TeacherDto() {
    }

    public TeacherDto(String firstName, String lastName, String egn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.egn = egn;
    }

    public Long getId() {
        return id;
    }

    public TeacherDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public TeacherDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public TeacherDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEgn() {
        return egn;
    }

    public TeacherDto setEgn(String egn) {
        this.egn = egn;
        return this;
    }

       @Override
    public String toString() {
        return "TeacherDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
