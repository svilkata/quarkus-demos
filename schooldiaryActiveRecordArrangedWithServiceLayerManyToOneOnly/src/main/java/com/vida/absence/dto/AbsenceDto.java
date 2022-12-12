package com.vida.absence.dto;

import com.vida.model.Absence;
import com.vida.model.Teacher;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class AbsenceDto {
    private Long id;
    @Positive
    private Integer countHours;
    @NotBlank
    private String absenceType;
    private LocalDateTime timeAdded;
    @Positive
    private Long teacherId;
    private String teacherFullName;

    public AbsenceDto() {
    }

    public AbsenceDto(Absence absence) {
        this.id = absence.getId();
        this.countHours = absence.getCountHours();
        this.absenceType = absence.getAbsenceTypeEnum().toString();
        this.timeAdded = absence.getTimeAdded();

        Teacher teacher = absence.getAssignment().getTeacher();

        this.teacherId = teacher.getId();
        this.teacherFullName = teacher.getFirstName() + " " + teacher.getLastName();
    }

    public Long getId() {
        return id;
    }

    public AbsenceDto setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getCountHours() {
        return countHours;
    }

    public AbsenceDto setCountHours(Integer countHours) {
        this.countHours = countHours;
        return this;
    }

    public String getAbsenceType() {
        return absenceType;
    }

    public AbsenceDto setAbsenceType(String absenceType) {
        this.absenceType = absenceType;
        return this;
    }

    public LocalDateTime getTimeAdded() {
        return timeAdded;
    }

    public AbsenceDto setTimeAdded(LocalDateTime timeAdded) {
        this.timeAdded = timeAdded;
        return this;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public AbsenceDto setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public AbsenceDto setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
        return this;
    }

//    public Absence toAbsenceEntity() {
//        Absence absence = new Absence();
//
//    }
}
