package com.vida.subject.dto;

import javax.validation.constraints.NotBlank;

public class SubjectDto {
    private Long id;
    @NotBlank(message = "Subject can not be empty")
    private String subject;

    public SubjectDto() {
    }

    public SubjectDto(String subject) {
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public SubjectDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getSubject() {
        return subject;
    }

    public SubjectDto setSubject(String subject) {
        this.subject = subject;
        return this;
    }
}
