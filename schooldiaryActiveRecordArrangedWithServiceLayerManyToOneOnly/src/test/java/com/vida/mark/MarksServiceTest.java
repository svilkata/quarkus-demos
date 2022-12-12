package com.vida.mark;

import com.vida.BasePrepForTests;
import com.vida.assignment.AssignmentService;
import com.vida.model.*;
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
class MarksServiceTest extends BasePrepForTests {
    @Inject
    MarksService marksService;
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
    void testGetAllMarksFromAStudentAndASubjectPageOneAndPageTwo() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        List<Assignment> assignmentsForSubjectAndKlasOfStudent = assignmentService.getAssignmentsForSubjectAndKlasOfStudent(
                subjectMath_1, student_1.getKlas());
        int page = 1;
        int size = 4;

        //Act
        List<Mark> allMarksFromAStudentAndASubjectPageOne = marksService.getAllMarksFromAStudentAndASubject(student_1, assignmentsForSubjectAndKlasOfStudent, page, size);
        List<Mark> allMarksFromAStudentAndASubjectPageTwo = marksService.getAllMarksFromAStudentAndASubject(student_1, assignmentsForSubjectAndKlasOfStudent, page + 1, size);

        //Assert
        assertEquals(4, allMarksFromAStudentAndASubjectPageOne.size());
        assertEquals(6, allMarksFromAStudentAndASubjectPageOne.get(0).getMark());
        assertEquals(6, allMarksFromAStudentAndASubjectPageOne.get(1).getMark());
        assertEquals(6, allMarksFromAStudentAndASubjectPageOne.get(2).getMark());
        assertEquals(6, allMarksFromAStudentAndASubjectPageOne.get(3).getMark());


        assertEquals(1, allMarksFromAStudentAndASubjectPageTwo.size());
        assertEquals(2, allMarksFromAStudentAndASubjectPageTwo.get(0).getMark());
    }

    @Test
    void testGetAllMarksFromAStudentAndASubjectWhenPageFourShouldReturnEmptyList() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        List<Assignment> assignmentsForSubjectAndKlasOfStudent = assignmentService.getAssignmentsForSubjectAndKlasOfStudent(
                subjectMath_1, student_1.getKlas());
        int page = 4;
        int size = 4;

        //Act
        List<Mark> allMarksFromAStudentAndASubjectPageFour = marksService.getAllMarksFromAStudentAndASubject(student_1, assignmentsForSubjectAndKlasOfStudent, page, size);

        //Assert
        assertEquals(0, allMarksFromAStudentAndASubjectPageFour.size());
    }

    @Test
    void testWriteMarkPerStudentPerSubject() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Assignment assignmentForSubjectTeacherAndKlas = assignmentService.getAssignmentForSubjectTeacherAndKlasOfStudent(
                subjectMath_1, student_1.getKlas(), teacher_1).orElse(null);
        int markToWriteSave = 5;

        //Act
        Mark mark = marksService.writeMarkPerStudentPerSubject(student_1, assignmentForSubjectTeacherAndKlas, markToWriteSave);

        //Assert
        assertEquals(assignmentForSubjectTeacherAndKlas, mark.getAssignment());
        assertEquals(student_1, mark.getStudent());
        assertEquals(markToWriteSave, mark.getMark());
    }
}