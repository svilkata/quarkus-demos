package com.vidasoft.diary.subject;

import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.validation.SubjectName;
import com.vidasoft.diary.validation.UniqueSubjectName;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Locale;

public class SubjectDTO {
    public Long id;

    @SubjectName
    @UniqueSubjectName
    public String name;

    public SubjectDTO() {
    }

    public SubjectDTO(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    public SubjectDTO(Subject subject) {
        this.id = subject.id;
        this.name = subject.name;
    }

    public Subject toSubject() {
        return new Subject(name.toLowerCase(Locale.ROOT));
    }
}
