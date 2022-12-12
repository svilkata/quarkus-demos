package com.vida.mark.dto;

import com.vida.model.Mark;
import com.vida.model.Teacher;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

public class MarkDto {
    private Long id;
    @Positive
    @Min(2)
    @Max(6)
    private Integer mark;
    private LocalDateTime timeAdded;
    @Positive
    private Long teacherId;
    private String teacherFullName;

    public MarkDto() {
    }


    public MarkDto(Mark markEntity) {
        this.id = markEntity.getId();
        this.mark = markEntity.getMark();
        this.timeAdded = markEntity.getTimeAdded();

        Teacher teacher = markEntity.getAssignment().getTeacher();

        this.teacherId = teacher.getId();
        this.teacherFullName = teacher.getFirstName() + " " + teacher.getLastName();
    }

    public Long getId() {
        return id;
    }

    public MarkDto setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getMark() {
        return mark;
    }

    public MarkDto setMark(Integer mark) {
        this.mark = mark;
        return this;
    }

    public LocalDateTime getTimeAdded() {
        return timeAdded;
    }

    public MarkDto setTimeAdded(LocalDateTime timeAdded) {
        this.timeAdded = timeAdded;
        return this;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public MarkDto setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public String getTeacherFullName() {
        return teacherFullName;
    }

    public MarkDto setTeacherFullName(String teacherFullName) {
        this.teacherFullName = teacherFullName;
        return this;
    }
}
