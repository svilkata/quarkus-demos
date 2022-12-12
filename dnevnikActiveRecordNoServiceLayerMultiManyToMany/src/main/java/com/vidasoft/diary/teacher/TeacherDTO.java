package com.vidasoft.diary.teacher;

import com.vidasoft.diary.model.Teacher;
import com.vidasoft.diary.validation.Identity;
import com.vidasoft.diary.validation.UniqueIdentity;

import javax.validation.constraints.NotBlank;

public class TeacherDTO {
    public Long id;

    @NotBlank(message = "First name is mandatory")
    public String firstName;

    @NotBlank(message = "Last name is mandatory")
    public String lastName;

    @UniqueIdentity
    @Identity
    public String identity;

    public TeacherDTO() {
    }

    public TeacherDTO(String firstName, String lastName, String identity) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.identity = identity;
    }

    public TeacherDTO(Teacher teacher) {
        id = teacher.id;
        firstName = teacher.firstName;
        lastName = teacher.lastName;
        identity = teacher.identity;
    }

//    @JsonbTransient
    public Teacher toTeacher() {
        return new Teacher(firstName, lastName, identity);
    }
}
