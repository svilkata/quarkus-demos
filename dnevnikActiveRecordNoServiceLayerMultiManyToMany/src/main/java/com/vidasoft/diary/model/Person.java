package com.vidasoft.diary.model;


import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Person extends AbstractEntity {
    @Column(name = "first_name", nullable = false)
    public String firstName;
    @Column(name = "last_name", nullable = false)
    public String lastName;

    @Column(unique = true)
    public String identity;

    public Person() {
    }

    public Person(String firstName, String lastName, String identity) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.identity = identity;
    }
}
