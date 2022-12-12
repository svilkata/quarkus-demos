package com.vida.model;

import com.vida.model.enums.AbsenceTypeEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "absences")
public class Absence extends AbstractEntity {
    //На кой студент
    @ManyToOne
    private Student student;

    //По кой assignment/предмет с учител с клас/
    @ManyToOne
    private Assignment assignment;

    //брой часове отсъствия, които учителя отбелязва
    @Column(name = "number_of_hours", nullable = false)
    private Integer countHours;
    @Column(name = "type_of_absence", nullable = false)
    private AbsenceTypeEnum absenceTypeEnum;
    @Column(name = "time_added")
    private LocalDateTime timeAdded;

    public Absence() {
    }

    public Absence(Student student, Assignment assignment, Integer countHours, AbsenceTypeEnum absenceTypeEnum) {
        this.student = student;
        this.assignment = assignment;
        this.countHours = countHours;
        this.absenceTypeEnum = absenceTypeEnum;
        this.timeAdded = LocalDateTime.now();
    }

    public Student getStudent() {
        return student;
    }

    public Absence setStudent(Student student) {
        this.student = student;
        return this;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public Absence setAssignment(Assignment assignment) {
        this.assignment = assignment;
        return this;
    }

    public Integer getCountHours() {
        return countHours;
    }

    public Absence setCountHours(Integer totalHours) {
        this.countHours = totalHours;
        return this;
    }

    public AbsenceTypeEnum getAbsenceTypeEnum() {
        return absenceTypeEnum;
    }

    public Absence setAbsenceTypeEnum(AbsenceTypeEnum absenceTypeEnum) {
        this.absenceTypeEnum = absenceTypeEnum;
        return this;
    }

    @Override
    public String toString() {
        return "Absence{" +
                "student=" + student +
                ", assignment=" + assignment +
                ", countHours=" + countHours +
                ", absenceTypeEnum=" + absenceTypeEnum +
                ", id=" + id +
                "} " + super.toString();
    }

    public LocalDateTime getTimeAdded() {
        return timeAdded;
    }

    public Absence setTimeAdded(LocalDateTime timeAdded) {
        this.timeAdded = timeAdded;
        return this;
    }
}
