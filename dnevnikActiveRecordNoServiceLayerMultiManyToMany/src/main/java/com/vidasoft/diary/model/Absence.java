package com.vidasoft.diary.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "absences")
public class Absence extends AbstractEntity {
    @ManyToOne
    public Student student;

    @ManyToOne
    public Clazz clazz;

    @ManyToOne
    public Subject subject;

    @ManyToOne
    public Teacher teacher;

    //брой часове отсъствия, които учителя отбелязва
    @Column(name = "absence_hours", nullable = false)
    public Integer absenceHours;

    @Column(name = "time_added", nullable = false)
    public LocalDateTime timeAdded;

    public Absence() {
    }

    public Absence(Integer absenceHours, Student student, Clazz clazz, Subject subject, Teacher teacher) {
        this.absenceHours = absenceHours;
        this.student = student;
        this.clazz = clazz;
        this.subject = subject;
        this.teacher = teacher;
        this.timeAdded = LocalDateTime.now();
    }
}
