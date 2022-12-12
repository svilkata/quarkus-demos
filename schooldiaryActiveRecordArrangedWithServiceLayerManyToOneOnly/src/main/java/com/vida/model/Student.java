package com.vida.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "students")
@NamedQuery(name = Student.GET_ALL_STUDENTS_FROM_A_KLASS_WITH_KLAS_INFO, query = "SELECT st FROM Student st WHERE st.klas.id= ?1")
//@NamedQuery(name = Student.GET_ALL_STUDENTS_WITHOUT_KLAS_INFO, query = "SELECT new com.vida.student.dto.OneStudentProjectionDto(st.id, st.firstName, st.lastName, st.egn) FROM Student st")
@NamedQuery(name = Student.GET_STUDENT_BY_EGN, query = "SELECT st FROM Student st WHERE st.egn = ?1")
//@NamedQuery(name = Student.GET_ALL_STUDENTS_FROM_A_KLAS_WITHOUT_KLAS_INFO, query = "SELECT new com.vida.student.dto.OneStudentProjectionDto(st.id, st.firstName, st.lastName, st.egn) FROM Student st WHERE st.klas = ?1")
public class Student extends Person {
    public static final String GET_ALL_STUDENTS_FROM_A_KLASS_WITH_KLAS_INFO = "getAllStudentsWithKlasInfo";
    public static final String GET_STUDENT_BY_EGN = "getStudentByEGN";


    @ManyToOne()
    private Klas klas;


    public Student() {
    }

    public Student(String firstName, String lastName, String egn) {
        super(firstName, lastName, egn);
    }

    public Klas getKlas() {
        return klas;
    }

    public Student setKlas(Klas klas) {
        this.klas = klas;
        return this;
    }

    public static Optional<Student> getStudentByEGN(String egn) {
        return Student.find("#" + Student.GET_STUDENT_BY_EGN, egn).firstResultOptional();
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + super.id +
                "} " + super.toString();
    }
}
