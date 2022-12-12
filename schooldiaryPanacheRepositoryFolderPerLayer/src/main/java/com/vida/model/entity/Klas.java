package com.vida.model.entity;

import com.vida.model.enums.KlasLetterEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@Entity
@Table(name = "klasses")
//@NamedQuery(name = "getKlasWithAllItsStudents", query = "SELECT DISTINCT k FROM Klas k JOIN FETCH k.students WHERE k.id = ?1")
@NamedQuery(name = "getKlasWithAllItsStudents", query = "SELECT k FROM Klas k WHERE k.id = ?1")
public class Klas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "klas_number")
    @NotNull
    @Positive
    private Integer klasNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "klas_letter")
    @NotNull
    private KlasLetterEnum klasLetterEnum;

    @OneToMany
    private Set<Student> students;

    public Klas() {
    }

    public Long getId() {
        return id;
    }

    public Klas setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getKlasNumber() {
        return klasNumber;
    }

    public Klas setKlasNumber(Integer klasNumber) {
        this.klasNumber = klasNumber;
        return this;
    }

    public KlasLetterEnum getKlasLetterEnum() {
        return klasLetterEnum;
    }

    public Klas setKlasLetterEnum(KlasLetterEnum klasLetterEnum) {
        this.klasLetterEnum = klasLetterEnum;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "id:" + id + "," +
                "klasNumber:" + klasNumber + "," +
                "klasLetterEnum:" + klasLetterEnum +
                '}';
    }

    public Set<Student> getStudents() {
        return students;
    }

    public Klas setStudents(Set<Student> students) {
        this.students = students;
        return this;
    }
}
