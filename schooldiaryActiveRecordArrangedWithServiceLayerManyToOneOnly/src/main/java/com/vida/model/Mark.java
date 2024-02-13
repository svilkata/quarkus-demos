package com.vida.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "marks")
public class Mark extends AbstractEntity {
    //to which student
    @ManyToOne
    private Student student;

    //from which assignment /school subject with concrete teacher and concrete klas/
    @ManyToOne
    private Assignment assignment;

    @Column(nullable = false)
    private Integer mark;
    @Column(name = "time_added", nullable = false)
    private LocalDateTime timeAdded;

    public Mark() {
    }

    public Mark(Student student, Assignment assignment, Integer mark) {
        this.student = student;
        this.assignment = assignment;
        this.mark = mark;
        this.timeAdded = LocalDateTime.now();
    }

    public Student getStudent() {
        return student;
    }

    public Mark setStudent(Student student) {
        this.student = student;
        return this;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public Mark setAssignment(Assignment assignment) {
        this.assignment = assignment;
        return this;
    }

    public Integer getMark() {
        return mark;
    }

    public Mark setMark(Integer mark) {
        this.mark = mark;
        return this;
    }

    public LocalDateTime getTimeAdded() {
        return timeAdded;
    }

    public Mark setTimeAdded(LocalDateTime timeAdded) {
        this.timeAdded = timeAdded;
        return this;
    }

    @Override
    public String toString() {
        return "Mark{" +
                "student=" + student +
                ", assignment=" + assignment +
                ", mark=" + mark +
                ", timeAdded=" + timeAdded +
                ", id=" + id +
                "} " + super.toString();
    }
}
