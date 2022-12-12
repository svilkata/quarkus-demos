package com.vidasoft.diary.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade extends AbstractEntity {
    @ManyToOne
    public Student student;

    @ManyToOne
    public Clazz clazz;

    @ManyToOne
    public Subject subject;

    @ManyToOne
    public Teacher teacher;

    @Column(nullable = false)
    public int grade;

    @Column(name = "time_added", nullable = false)
    public LocalDateTime timeAdded;

    public Grade() {
    }

    public Grade(int grade, Student student, Clazz clazz, Subject subject, Teacher teacher) {
        this.grade = grade;
        this.student = student;
        this.clazz = clazz;
        this.subject = subject;
        this.teacher = teacher;
        this.timeAdded = LocalDateTime.now();
    }
}
