package com.vida.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.util.List;

/*
Each object Assignment keeps info about the relevant school subject - but school subject for a concrete klas and with concrete teacher!
* */

@Entity
@Table(name = "assignments")
@NamedQuery(name = Assignment.GET_ALL_SUBJECTS_ASSIGNED_TO_A_TEACHER, query = "SELECT DISTINCT asgn.subject FROM Assignment asgn WHERE asgn.teacher = ?1")
@NamedQuery(name = Assignment.GET_ALL_KLASSES_FOR_A_TEACHER, query = "SELECT DISTINCT asgn.klas FROM Assignment asgn WHERE asgn.teacher = ?1")
@NamedQuery(name = Assignment.GET_ALL_SUBJECTS_FOR_TEACHER_AND_KLAS, query = "SELECT asgn.subject FROM Assignment asgn WHERE asgn.teacher = ?1 and asgn.klas = ?2")
@NamedQuery(name = Assignment.GET_ALL_KLASSES_FOR_TEACHER_AND_SUBJECT, query = "SELECT asgn.klas FROM Assignment asgn WHERE asgn.teacher = ?1 and asgn.subject = ?2")
@NamedQuery(name = Assignment.GET_ALL_TEACHERS_FOR_A_KLAS, query = "SELECT DISTINCT asgn.teacher FROM Assignment asgn WHERE asgn.klas = ?1")
public class Assignment extends AbstractEntity {
    public static final String GET_ALL_SUBJECTS_ASSIGNED_TO_A_TEACHER = "getAllSubjectsAssignedToATeacher";
    public static final String GET_ALL_KLASSES_FOR_A_TEACHER = "getAllKlassesForATeacher";
    public static final String GET_ALL_SUBJECTS_FOR_TEACHER_AND_KLAS = "getAllSubjectsForATeacherAndAKlas";
    public static final String GET_ALL_KLASSES_FOR_TEACHER_AND_SUBJECT = "getAllKlassesForATeacherAndASubject";
    public static final String GET_ALL_TEACHERS_FOR_A_KLAS = "getAllteachersForAKlas";

    @ManyToOne
    private Subject subject;

    @ManyToOne
    private Klas klas;

    @ManyToOne
    private Teacher teacher;

    public Assignment() {
    }

    public Assignment(Subject subject, Klas klas, Teacher teacher) {
        this.subject = subject;
        this.klas = klas;
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public Assignment setSubject(Subject subject) {
        this.subject = subject;
        return this;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Assignment setTeacher(Teacher teacher) {
        this.teacher = teacher;
        return this;
    }

    public Klas getKlas() {
        return klas;
    }

    public Assignment setKlas(Klas klas) {
        this.klas = klas;
        return this;
    }

    public static List<Subject> getAllSubjectsAssignedToATeacher(Teacher teacherToCheck, int page, int size) {
        return Assignment.<Subject>find("#" + Assignment.GET_ALL_SUBJECTS_ASSIGNED_TO_A_TEACHER, teacherToCheck)
                .page(page, size).stream().toList();
    }

    public static List<Klas> getAllKlassesAssignedToATeacher(Teacher teacherToCheck, int page, int size) {
        return Assignment.<Klas>find("#" + Assignment.GET_ALL_KLASSES_FOR_A_TEACHER, teacherToCheck)
                .page(page, size).list();
    }

    public static List<Subject> getAllSubjectPerTeacherPerKlas(Teacher teacherToCheck, Klas klasToCheck, int page, int size) {
        return Assignment.<Subject>find("#" + Assignment.GET_ALL_SUBJECTS_FOR_TEACHER_AND_KLAS, teacherToCheck, klasToCheck)
                .page(page, size).list();
    }

    public static List<Klas> getAllKlassesPerTeacherPerSubject(Teacher teacherToCheck, Subject subjectToCheck, int page, int size) {
        return Assignment.<Klas>find("#" + Assignment.GET_ALL_KLASSES_FOR_TEACHER_AND_SUBJECT, teacherToCheck, subjectToCheck)
                .page(page, size).list();
    }

    public static List<Teacher> getAllTeachersPerKlas(Klas klasToCheck, int page, int size) {
        return Assignment.<Teacher>find("#" + Assignment.GET_ALL_TEACHERS_FOR_A_KLAS, klasToCheck)
                .page(page, size).list();
    }


    @Override
    public String toString() {
        return "Assignment{" +
                "id=" + id +
                ", subject=" + subject +
                ", klas=" + klas +
                ", teacher=" + teacher +
                '}';
    }
}
