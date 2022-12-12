package com.vidasoft.diary.model;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static com.vidasoft.diary.model.Clazz.*;

@Entity
@Table(name = "clazzes")
@NamedQuery(query = "SELECT c FROM Clazz c LEFT JOIN c.teachers t WHERE t.id = ?1", name = GET_CLAZZES_BY_TEACHER)
@NamedQuery(query = "SELECT c FROM Clazz c LEFT JOIN c.teachers t LEFT JOIN t.subjects s WHERE t.id = ?1 AND s.id = ?2", name = GET_CLAZZES_BY_TEACHER_AND_SUBJECT)
public class Clazz extends AbstractEntity {
    public static final String GET_CLAZZES_BY_TEACHER = "Clazz.getClazzesByTeacher";
    public static final String GET_CLAZZES_BY_TEACHER_AND_SUBJECT = "Clazz.getClazzesByTeacherAndSubject";

    @Column(name = "clazz_number", nullable = false)
    public String clazzNumber;
    @Column(name = "subclaz_initial", nullable = false)
    public String subclazzInitial;

    //BACK_SIDE of the BIDIRECTIONAL subjects <-> clazzes
    @ManyToMany(mappedBy = "clazzes")
    public Set<Subject> subjects = new LinkedHashSet<>();


    //!!! Където декларираме така, оттам merge-ваме добавяме елементи, или с други думи:
    //добавяме на clazz teacher-и
    //FRONT_SIDE of the BIDIRECTIONAL clazzes <-> teachers
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "clazz_teacher",
            joinColumns = @JoinColumn(name = "clazz_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    )
    public Set<Teacher> teachers = new LinkedHashSet<>();

    public Clazz() {
    }

    public Clazz(String clazzNumber, String subclazzInitial) {
        this.clazzNumber = clazzNumber;
        this.subclazzInitial = subclazzInitial;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "clazzNumber='" + clazzNumber + '\'' +
                ", subclazzInitial='" + subclazzInitial + '\'' +
                ", id=" + id +
                "} ";
    }
}
