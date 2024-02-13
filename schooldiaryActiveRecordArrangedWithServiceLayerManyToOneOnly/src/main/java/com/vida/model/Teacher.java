package com.vida.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "teachers")
public class Teacher extends Person {

    public Teacher() {
    }

    public Teacher(String firstName, String lastName, String egn) {
        super(firstName, lastName, egn);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + super.id +
                "} " + super.toString();
    }
}
