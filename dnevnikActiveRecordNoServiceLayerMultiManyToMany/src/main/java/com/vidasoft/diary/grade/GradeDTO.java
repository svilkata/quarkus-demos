package com.vidasoft.diary.grade;

import com.vidasoft.diary.model.Grade;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class GradeDTO {
    public Long id;

    @Positive
    @Min(2)
    @Max(6)
    public Integer grade;

    public LocalDateTime timeAdded;

    public String teacherFullName;

    public GradeDTO() {
    }

    public GradeDTO(Grade grade) {
        this.id = grade.id;
        this.grade = grade.grade;
        this.timeAdded = grade.timeAdded;
        this.teacherFullName = grade.teacher.firstName + " " + grade.teacher.lastName;
    }
}
