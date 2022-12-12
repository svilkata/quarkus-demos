package com.vidasoft.diary.student;

import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.validation.Identity;
import com.vidasoft.diary.validation.UniqueIdentity;

import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import java.util.Optional;

public class StudentDTO {

    public Long id;

    @NotBlank(message = "First name is mandatory")
    public String firstName;

    @NotBlank(message = "Last name is mandatory")
    public String lastName;

    @UniqueIdentity
    @Identity
    public String identity;

    @JsonbProperty(value = "clazz")
    public String clazz;

    public StudentDTO() {

    }

    public StudentDTO(String firstName, String lastName, String identity) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.identity = identity;
    }

    public StudentDTO(Student student) {
        id = student.id;
        firstName = student.firstName;
        lastName = student.lastName;
        identity = student.identity;
        clazz = Optional.ofNullable(student.clazz).map(c -> String.format("%s%s", c.clazzNumber, c.subclazzInitial)).orElse(null);
    }

//    @JsonbTransient
    public Student toStudent() {
        return new Student(firstName, lastName, identity);
    }
}
