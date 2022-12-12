package com.vidasoft.diary.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.transaction.Transactional;

@Entity
@Table(name = "students")
public class Student extends Person {

    @ManyToOne
    public Clazz clazz;

    public Student() {
    }

    public Student(String firstName, String lastName, String identity) {
        super(firstName, lastName, identity);
    }

    public Student setClazz(Clazz clazz) {
        this.clazz = clazz;
        return this;
    }
}
