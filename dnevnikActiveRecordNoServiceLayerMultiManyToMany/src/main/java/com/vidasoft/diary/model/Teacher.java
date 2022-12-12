package com.vidasoft.diary.model;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.vidasoft.diary.model.Teacher.GET_TEACHERS_BY_SUBJECT_AND_CLAZZ;

@Entity
@Table(name = "teachers")
@NamedQuery(query = "SELECT t FROM Teacher t LEFT JOIN t.subjects sb LEFT JOIN sb.clazzes c WHERE sb.id = ?1 AND c.id = ?2", name = GET_TEACHERS_BY_SUBJECT_AND_CLAZZ)
public class Teacher extends Person {
    public static final String GET_TEACHERS_BY_SUBJECT_AND_CLAZZ = "Teacher.getTeachersBySubjectAndClazz";

    //BACK_SIDE of the BIDIRECTIONAL subjects <-> teachers
    @ManyToMany(mappedBy = "teachers")
    public Set<Subject> subjects = new LinkedHashSet<>();

    //BACK_SIDE of the BIDIRECTIONAL clazzes <-> teachers
    @ManyToMany(mappedBy = "teachers")
    public Set<Clazz> clazzes = new LinkedHashSet<>();

    public Teacher() {
    }

    public Teacher(String firstName, String lastName, String identity) {
        super(firstName, lastName, identity);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", identity='" + identity + '\'' +
                ", id=" + id +
                "} ";
    }
}
