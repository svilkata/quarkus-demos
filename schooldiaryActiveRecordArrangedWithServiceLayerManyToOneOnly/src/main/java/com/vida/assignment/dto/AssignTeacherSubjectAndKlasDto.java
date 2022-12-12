package com.vida.assignment.dto;

import javax.validation.constraints.Positive;

public class AssignTeacherSubjectAndKlasDto {
    private Long id;
    @Positive
    private Long teacherId;
    @Positive
    private Long subjectId;
    @Positive
    private Long klasId;

    public AssignTeacherSubjectAndKlasDto() {
    }

    public AssignTeacherSubjectAndKlasDto(Long teacherId, Long subjectId, Long klasId) {
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.klasId = klasId;
    }

    public Long getId() {
        return id;
    }

    public AssignTeacherSubjectAndKlasDto setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public AssignTeacherSubjectAndKlasDto setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
        return this;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public AssignTeacherSubjectAndKlasDto setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
        return this;
    }

    public Long getKlasId() {
        return klasId;
    }

    public AssignTeacherSubjectAndKlasDto setKlasId(Long klasId) {
        this.klasId = klasId;
        return this;
    }
}
