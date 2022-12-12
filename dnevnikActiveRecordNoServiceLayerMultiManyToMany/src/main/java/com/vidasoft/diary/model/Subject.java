package com.vidasoft.diary.model;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import static com.vidasoft.diary.model.Subject.*;


@Entity
@Table(name = "subjects")
@NamedQuery(query = "SELECT sb FROM Subject sb LEFT JOIN sb.teachers t LEFT JOIN t.clazzes c WHERE t.id = ?1 AND c.id = ?2", name = GET_SUBJECTS_BY_TEACHER_AND_CLAZZ)
@NamedQuery(query = "SELECT sb FROM Subject sb LEFT JOIN sb.teachers t WHERE t.id = ?1", name = GET_SUBJECTS_BY_TEACHER)
@NamedQuery(query = "SELECT sb FROM Subject sb LEFT JOIN sb.teachers t LEFT JOIN sb.clazzes c WHERE sb.id = ?1 AND t.id = ?2 AND c.id = ?3", name = GET_SUBJECT_BY_SUBJECT_AND_TEACHER_AND_CLAZZ)
//@NamedQuery(query = "SELECT c FROM Clazz c LEFT JOIN c.subjects sb LEFT JOIN c.teachers t WHERE sb.id = ?1 AND t.id = ?2", name = GET_CLAZZES_FROM_SUBJECT_BY_TEACHER)
//@NamedQuery(query = "SELECT t FROM Teacher t LEFT JOIN t.subjects sb LEFT JOIN t.clazzes c  WHERE sb.id = ?1 AND c.id = ?2", name = GET_TEACHERS_FROM_SUBJECT_BY_CLAZZ)
public class Subject extends AbstractEntity {
    public static final String GET_SUBJECTS_BY_TEACHER_AND_CLAZZ = "Subject.getSubjectsByTeacherAndClazz";
    public static final String GET_SUBJECTS_BY_TEACHER = "Subject.getSubjectsByTeacher";
    public static final String GET_SUBJECT_BY_SUBJECT_AND_TEACHER_AND_CLAZZ = "Subject.getSubjectBySubjectTeacherAndClazz";

//    public static final String GET_CLAZZES_FROM_SUBJECT_BY_TEACHER = "Subject.getClazzesFromSubjectByTeacher";
//    public static final String GET_TEACHERS_FROM_SUBJECT_BY_CLAZZ = "Subject.getTeachersFromSubjectByClazz";


    @Column(name = "subject_name", unique = true, nullable = false)
    public String name;

    //!!! Мerge-ваме добавяме елементи отттук, или с други думи:
    //добавяме на subject нови teachers
    //FRONT_SIDE of the BIDIRECTIONAL subjects <-> teachers
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "subject_teacher",
            joinColumns = @JoinColumn(name = "subject_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "teacher_id", referencedColumnName = "id")
    )
    public Set<Teacher> teachers = new LinkedHashSet<>();


    //!!! Мerge-ваме добавяме елементи отттук, или с други думи:
    //добавяме на subject нови clazzes
    //FRONT_SIDE of the BIDIRECTIONAL subjects <-> clazzes
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "subject_clazz",
            joinColumns = @JoinColumn(name = "subject_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "clazz_id", referencedColumnName = "id")
    )
    public Set<Clazz> clazzes = new LinkedHashSet<>();

    public Subject() {
    }

    public Subject(String name) {
        this.name = name.toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                ", id=" + id +
                "} ";
    }
}
