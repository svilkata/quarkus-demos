package com.vida.absence;

import com.vida.BasePrepForTests;
import com.vida.assignment.AssignmentService;
import com.vida.mark.MarksService;
import com.vida.model.*;
import com.vida.model.enums.AbsenceTypeEnum;
import com.vida.student.StudentService;
import com.vida.subject.SubjectService;
import com.vida.teacher.TeacherService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@Tag("integration")
@Transactional
class AbsenceServiceTest extends BasePrepForTests {
    @Inject
    AbsenceService absenceService;
    @Inject
    StudentService studentService;
    @Inject
    SubjectService subjectService;
    @Inject
    AssignmentService assignmentService;
    @Inject
    TeacherService teacherService;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
    }

    @Override
    @AfterEach
    protected void tearDown() {
        super.tearDown();
    }

    @Test
    void testGetAllAbsencesFromAStudentAndASubjectPageOneAndPageTwo() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        List<Assignment> assignmentsForSubjectAndKlasOfStudent = assignmentService.getAssignmentsForSubjectAndKlasOfStudent(
                subjectMath_1, student_1.getKlas());
        int page = 1;
        int size = 4;

        //Act
        List<Absence> allAbsencesFromAStudentAndASubjectPageOne = absenceService.getAllAbsencesFromAStudentAndASubject(student_1, assignmentsForSubjectAndKlasOfStudent, page, size);
        List<Absence> allAbsencesFromAStudentAndASubjectPageTwo = absenceService.getAllAbsencesFromAStudentAndASubject(student_1, assignmentsForSubjectAndKlasOfStudent, page + 1, size);

        //Assert
        assertEquals(4, allAbsencesFromAStudentAndASubjectPageOne.size());
        assertEquals(AbsenceTypeEnum.OLYMPIAD, allAbsencesFromAStudentAndASubjectPageOne.get(0).getAbsenceTypeEnum());
        assertEquals(AbsenceTypeEnum.OLYMPIAD, allAbsencesFromAStudentAndASubjectPageOne.get(1).getAbsenceTypeEnum());
        assertEquals(AbsenceTypeEnum.HOSPITAL, allAbsencesFromAStudentAndASubjectPageOne.get(2).getAbsenceTypeEnum());
        assertEquals(AbsenceTypeEnum.OLYMPIAD, allAbsencesFromAStudentAndASubjectPageOne.get(3).getAbsenceTypeEnum());


        assertEquals(1, allAbsencesFromAStudentAndASubjectPageTwo.size());
        assertEquals(AbsenceTypeEnum.STUDENTGUILTY, allAbsencesFromAStudentAndASubjectPageTwo.get(0).getAbsenceTypeEnum());
    }


    @Test
    void testGetAllAbsencesFromAStudentAndASubjectWhenPageFourShouldReturnEmptyList() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        List<Assignment> assignmentsForSubjectAndKlasOfStudent = assignmentService.getAssignmentsForSubjectAndKlasOfStudent(
                subjectMath_1, student_1.getKlas());
        int page = 4;
        int size = 4;

        //Act
        List<Absence> allAbsencesFromAStudentAndASubjectPageFour = absenceService.getAllAbsencesFromAStudentAndASubject(student_1, assignmentsForSubjectAndKlasOfStudent, page, size);

        //Assert
        assertEquals(0, allAbsencesFromAStudentAndASubjectPageFour.size());
    }

    @Test
    void testWriteAbsencePerStudentPerSubject() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Assignment assignmentForSubjectTeacherAndKlas = assignmentService.getAssignmentForSubjectTeacherAndKlasOfStudent(
                subjectMath_1, student_1.getKlas(), teacher_1).orElse(null);
        Integer absenceHours = 2;

        Absence absencePrep = new Absence(student_1, assignmentForSubjectTeacherAndKlas, absenceHours,
                AbsenceTypeEnum.STUDENTGUILTY);

        //Act
        Absence absenceSaved = absenceService.writeAbsencePerStudentPerSubject(absencePrep);

        //Assert
        assertEquals(assignmentForSubjectTeacherAndKlas, absenceSaved.getAssignment());
        assertEquals(student_1, absenceSaved.getStudent());
        assertEquals(AbsenceTypeEnum.STUDENTGUILTY, absenceSaved.getAbsenceTypeEnum());
        assertEquals(absenceHours, absenceSaved.getCountHours());
    }
}