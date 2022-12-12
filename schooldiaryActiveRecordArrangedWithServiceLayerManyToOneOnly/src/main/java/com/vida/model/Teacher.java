package com.vida.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.UUID;

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
