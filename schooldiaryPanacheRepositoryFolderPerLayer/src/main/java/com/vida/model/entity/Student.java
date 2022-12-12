package com.vida.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@Table(name = "students")
@NamedQuery(name = "getAllStudentsByKlasId", query = "SELECT st FROM Student st WHERE st.klas.id= ?1")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is mandatory")
    private String firstName;
    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is mandatory")
    private String lastName;
    @Column(name = "egn", nullable = false, unique = true)
    @NotBlank(message = "EGN is mandatory")
    private String EGN;

    @ManyToOne
    private Klas klas;


    public Student() {
    }

    public Long getId() {
        return id;
    }

    public Student setId(Long id) {
        this.id = id;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Student setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Student setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEGN() {
        return EGN;
    }

    public Student setEGN(String EGN) {
        this.EGN = EGN;
        return this;
    }

    public Klas getKlas() {
        return klas;
    }

    public Student setKlas(Klas klas) {
        this.klas = klas;
        return this;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", EGN='" + EGN + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return id.equals(student.id) && firstName.equals(student.firstName) && lastName.equals(student.lastName) && EGN.equals(student.EGN) && Objects.equals(klas, student.klas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, EGN, klas);
    }
}
