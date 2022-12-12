package com.vida.assignment;

import com.vida.BasePrepForTests;
import com.vida.klas.KlasService;
import com.vida.model.Assignment;
import com.vida.model.Klas;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import com.vida.subject.SubjectService;
import com.vida.teacher.TeacherService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@Tag("integration")
@Transactional
class AssignmentServiceTest extends BasePrepForTests {
    @Inject
    TeacherService teacherService;
    @Inject
    SubjectService subjectService;
    @Inject
    KlasService klasService;
    @Inject
    AssignmentService assignmentService;

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
    void testAssignATeacherASubjectAndAKlas() {
        //Arrange
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Subject subject_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Klas klas1D = klasService.getAllKlasses(1, 4).get(3);

        //Act and Assert
        assignmentService.assignATeacherASubjectAndAKlas(teacher_1, klas1D, subject_1)
                .ifPresentOrElse(
                        asgnm -> assertEquals(assignmentService.getAssignmentForSubjectTeacherAndKlasOfStudent(subject_1, klas1D, teacher_1).get(), asgnm),
                        () -> Assertions.fail());
    }

    @Test
    void testAssignATeacherASubjectAndAKlasWhenSuchAssignmentAlreadyExistShoudReturnOptionalEmpty() {
        //Arrange
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Subject subject_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        //Act
        Optional<Assignment> assignment = assignmentService.assignATeacherASubjectAndAKlas(teacher_1, klas1A, subject_1);

        //Assert
        assertTrue(assignment.isEmpty());
    }

    //We can check also third page which will return empty list
    @Test
    void testGetSubjectsForTeacherWhenPageOne() {
        //Arrange
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        //Act
        List<Subject> subjectsForTeacher = assignmentService.getSubjectsForTeacher(teacher_1, 1, 4);

        //Assert
        assertEquals(3, subjectsForTeacher.size());
        assertEquals(subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null), subjectsForTeacher.get(0));
        assertEquals(subjectService.getSubjectBySubjectString(SUBJECTLITERA_2).orElse(null), subjectsForTeacher.get(1));
        assertEquals(subjectService.getSubjectBySubjectString(SUBJECTSPORT_6).orElse(null), subjectsForTeacher.get(2));
    }


    @Test
    void testGetSubjectsForTeacherWhenPageOneAndWhenTeacherDoesNotHaveAssignmentYetShouldReturnEmptyList() {
        //Arrange
        Teacher teacher_5 = teacherService.getTeacherByEgn(EGNTEACHER_5).orElse(null);

        //Act
        List<Subject> subjectsForTeacher = assignmentService.getSubjectsForTeacher(teacher_5, 1, 4);

        //Assert
        assertEquals(0, subjectsForTeacher.size());
    }


    //We can check also third page which will return empty list
    @Test
    void testGetKlassesForTeacherWhenPageOne() {
        //Arrange
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        List<Klas> allKlassesPageOne = klasService.getAllKlasses(1, 4);
        Klas klas1A = allKlassesPageOne.get(0);
        Klas klas1B = allKlassesPageOne.get(1);
        Klas klas1C = allKlassesPageOne.get(2);

        //Act
        List<Klas> klassesForTeacher = assignmentService.getKlassesForTeacher(teacher_1, 1, 4);

        //Assert
        assertEquals(3, klassesForTeacher.size());
        assertEquals(klas1A, klassesForTeacher.get(0));
        assertEquals(klas1B, klassesForTeacher.get(1));
        assertEquals(klas1C, klassesForTeacher.get(2));
    }

    @Test
    void testGetKlassesForTeacherWhenPageOneAndWhenTeacherDoesNotHaveAssignmentYetShouldReturnEmptyList() {
        //Arrange
        Teacher teacher_5 = teacherService.getTeacherByEgn(EGNTEACHER_5).orElse(null);

        //Act
        List<Klas> klassesForTeacher = assignmentService.getKlassesForTeacher(teacher_5, 1, 4);

        //Assert
        assertEquals(0, klassesForTeacher.size());
    }

    //We can check also third page which will return empty list
    @Test
    void testGetSubjectsForTeacherAndKlasWhenPageOne() {
        //Arrange
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        //Act
        List<Subject> subjectsForTeacherAndKlas = assignmentService.getSubjectsForTeacherAndKlas(teacher_1, klas1A, 1, 4);

        //Assert
        assertEquals(2, subjectsForTeacherAndKlas.size());
        assertEquals(subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null), subjectsForTeacherAndKlas.get(0));
        assertEquals(subjectService.getSubjectBySubjectString(SUBJECTLITERA_2).orElse(null), subjectsForTeacherAndKlas.get(1));
    }

    @Test
    void testGetSubjectsForTeacherAndKlasWhenPageOneAndTeacherDoesNotHaveAssignmentForThisKlasYetShouldReturnEmptyList() {
        //Arrange
        Teacher teacher_5 = teacherService.getTeacherByEgn(EGNTEACHER_5).orElse(null);
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        //Act
        List<Subject> subjectsForTeacherAndKlas = assignmentService.getSubjectsForTeacherAndKlas(teacher_5, klas1A, 1, 4);

        //Assert
        assertEquals(0, subjectsForTeacherAndKlas.size());
    }


    //We can check also third page which will return empty list
    @Test
    void testGetKlassesForTeacherAndSubjectPageOne() {
        //Arrange
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        List<Klas> allKlassesPageOne = klasService.getAllKlasses(1, 4);
        Klas klas1A = allKlassesPageOne.get(0);
        Klas klas1B = allKlassesPageOne.get(1);

        //Act
        List<Klas> klassesForTeacherAndSubject = assignmentService.getKlassesForTeacherAndSubject(teacher_1, subjectMath_1, 1, 4);

        //Assert
        assertEquals(2, klassesForTeacherAndSubject.size());
        assertEquals(klas1A, klassesForTeacherAndSubject.get(0));
        assertEquals(klas1B, klassesForTeacherAndSubject.get(1));
    }

    @Test
    void testGetKlassesForTeacherAndSubjectWhenPageOneAndTeacherDoesNotHaveAssignmentForThisSubjectYetShouldReturnEmptyList() {
        //Arrange
        Teacher teacher_5 = teacherService.getTeacherByEgn(EGNTEACHER_5).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        //Act
        List<Klas> klassesForTeacherAndSubject = assignmentService.getKlassesForTeacherAndSubject(teacher_5, subjectMath_1, 1, 4);

        //Assert
        assertEquals(0, klassesForTeacherAndSubject.size());
    }

    //We can check also third page which will return empty list
    @Test
    void testGetAllTeachersByKlasPageOne() {
        //Arrange
        List<Klas> allKlassesPageOne = klasService.getAllKlasses(1, 4);
        Klas klas1A = allKlassesPageOne.get(0);

        //Act
        List<Teacher> allTeachersByKlas = assignmentService.getAllTeachersByKlas(klas1A, 1, 4);

        //Assert
        assertEquals(4, allTeachersByKlas.size());
        assertEquals(teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null), allTeachersByKlas.get(0));
        assertEquals(teacherService.getTeacherByEgn(EGNTEACHER_2).orElse(null), allTeachersByKlas.get(1));
        assertEquals(teacherService.getTeacherByEgn(EGNTEACHER_3).orElse(null), allTeachersByKlas.get(2));
        assertEquals(teacherService.getTeacherByEgn(EGNTEACHER_4).orElse(null), allTeachersByKlas.get(3));
    }

    @Test
    void testGetAllTeachersByKlasWhenPageOneAndKlasDoesNotHaveAssignmentForAnyTeacherYetShouldReturnEmptyList() {
        //Arrange
        List<Klas> allKlassesPageOne = klasService.getAllKlasses(2, 4);
        Klas klas2A = allKlassesPageOne.get(0);

        //Act
        List<Teacher> allTeachersByKlas = assignmentService.getAllTeachersByKlas(klas2A, 1, 4);

        //Assert
        assertEquals(0, allTeachersByKlas.size());
    }


    @Test
    void testGetAssignmentsForSubjectAndKlasOfStudent() {
        //Arrange
        List<Klas> allKlassesPageOne = klasService.getAllKlasses(1, 4);
        Klas klas1A = allKlassesPageOne.get(0);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        //Act
        List<Assignment> assignmentsForSubjectAndKlasOfStudent = assignmentService.getAssignmentsForSubjectAndKlasOfStudent(subjectMath_1, klas1A);

        //Assert
        //For klas 1A on the subject math, 2 teachers teach
        assertEquals(2, assignmentsForSubjectAndKlasOfStudent.size());
        assertEquals(teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null), assignmentsForSubjectAndKlasOfStudent.get(0).getTeacher());
        assertEquals(teacherService.getTeacherByEgn(EGNTEACHER_2).orElse(null), assignmentsForSubjectAndKlasOfStudent.get(1).getTeacher());
    }

    @Test
    void testGetAssignmentsForSubjectAndKlasOfStudentWhenNoAssignmentPresentShouldReturnEmptyList() {
        //Arrange
        List<Klas> allKlassesPageOne = klasService.getAllKlasses(2, 4);
        Klas klas2A = allKlassesPageOne.get(0);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        //Act
        List<Assignment> assignmentsForSubjectAndKlasOfStudent = assignmentService.getAssignmentsForSubjectAndKlasOfStudent(subjectMath_1, klas2A);

        //Assert
        //For klas 1A on the subject math, 2 teachers teach
        assertEquals(0, assignmentsForSubjectAndKlasOfStudent.size());
    }


    @Test
    void testGetAssignmentForSubjectTeacherAndKlasOfStudent() {
        //Arrange
        List<Klas> allKlassesPageOne = klasService.getAllKlasses(1, 4);
        Klas klas1A = allKlassesPageOne.get(0);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        //Act
        Assignment assignmentForSubjectTeacherAndKlasOfStudent = assignmentService.getAssignmentForSubjectTeacherAndKlasOfStudent(subjectMath_1, klas1A, teacher_1).orElse(null);

        //Assert
        assertTrue(assignmentForSubjectTeacherAndKlasOfStudent != null);
        assertEquals(Assignment.<Assignment>findById(assignmentForSubjectTeacherAndKlasOfStudent.getId()), assignmentForSubjectTeacherAndKlasOfStudent);
    }

    @Test
    void testGetAssignmentForSubjectTeacherAndKlasOfStudentWhenNoAssignmentShouldReturnOptionalEmpty() {
        //Arrange
        List<Klas> allKlassesPageOne = klasService.getAllKlasses(2, 4);
        Klas klas2A = allKlassesPageOne.get(0);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        //Act
        Optional<Assignment> assignmentForSubjectTeacherAndKlasOfStudent = assignmentService.getAssignmentForSubjectTeacherAndKlasOfStudent(subjectMath_1, klas2A, teacher_1);

        //Assert
        assertTrue(assignmentForSubjectTeacherAndKlasOfStudent.isEmpty());
    }
}