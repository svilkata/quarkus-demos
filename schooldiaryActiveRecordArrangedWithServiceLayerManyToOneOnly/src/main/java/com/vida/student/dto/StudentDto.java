package com.vida.student.dto;

import com.vida.model.Student;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Optional;

public class StudentDto {
    private Long id;

    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    @NotBlank(message = "EGN is mandatory")
    @Size(min = 10, max = 10, message = "EGN should be 10 symbols")
    private String egn;

    private Long klasId;
    @JsonbProperty(value = "klasName")
    private String klasName;

    public StudentDto() {
    }

    public StudentDto(Long id, String firstName, String lastName, String egn) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.egn = egn;
    }

    public StudentDto(Student student) {
        id = student.getId();
        firstName = student.getFirstName();
        lastName = student.getLastName();
        egn = student.getEgn();
        klasId = student.getKlas() == null ? null : student.getKlas().getId();
        klasName = Optional.ofNullable(student.getKlas()).map(kl -> String.format("%s%s", kl.getKlasNumber(), kl.getKlasLetterEnum().name()))
                .orElse(null);
    }


//    @JsonbTransient
    public Student toStudent() {
        return new Student(firstName, lastName, egn);
    }

    public Long getId() {
        return id;
    }

    public StudentDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public StudentDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public StudentDto setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEgn() {
        return egn;
    }

    public StudentDto setEgn(String egn) {
        this.egn = egn;
        return this;
    }

    public Long getKlasId() {
        return klasId;
    }

    public StudentDto setKlasId(Long klasId) {
        this.klasId = klasId;
        return this;
    }

    public String getKlasName() {
        return klasName;
    }

    public StudentDto setKlasName(String klasName) {
        this.klasName = klasName;
        return this;
    }



    @Override
    public String toString() {
        return "StudentDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", egn='" + egn + '\'' +
                '}';
    }
}
