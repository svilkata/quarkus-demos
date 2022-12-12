package com.vida.teacher;

import com.vida.BasePrepForTests;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
@Transactional
class TeacherServiceTest extends BasePrepForTests {
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
    @Order(1)
    void testGetTeacherById() {
        //Arrange & Act
        Teacher teacherByEgn = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Teacher teacherById = teacherService.getTeacherById(teacherByEgn.getId()).orElse(null);

        //Assert
        assertTrue(teacherById != null);
        assertEquals(teacherById, teacherService.getTeacherById(teacherById.getId()).get());
    }

    @Test
    @Order(2)
    void testGetAllTeachersWhenPageOneAndPageTwo() {
        // Act
        List<Teacher> allTeachersFirstPage = teacherService.getAllTeachers(1, 4);
        List<Teacher> allTeachersSecondPage = teacherService.getAllTeachers(2, 4);

        //Assert
        assertEquals(4, allTeachersFirstPage.size());
        assertEquals(EGNTEACHER_1, allTeachersFirstPage.get(0).getEgn());

        assertEquals(1, allTeachersSecondPage.size());
        //Check 5th teacher - second page the only one teacher
        assertEquals(allTeachersFirstPage.get(0), teacherService.getTeacherById(allTeachersFirstPage.get(0).getId()).get());
    }

    @Test
    @Order(3)
    void testGetAllTeachersWhenPageFourShouldReturnEmptyList() {
        // Act
        List<Teacher> allTeachersFirstPage = teacherService.getAllTeachers(4, 4);

        //Assert
        assertEquals(0, allTeachersFirstPage.size());
    }


    @Test
    @Order(4)
    void testAddNewTeacher() {
        //Arrange
        Teacher teacherNew = new Teacher("Dimitar", "Dimitrov", "dimidimidi");

        //Act
        Teacher teacherAddedSaved = teacherService.addNewTeacher(teacherNew).orElse(null);

        //Assert
        assertTrue(teacherAddedSaved != null);
        assertEquals(teacherAddedSaved, teacherService.getTeacherById(teacherAddedSaved.getId()).get());
    }

    @Test
    @Order(5)
    void testAddNewTeacherWhenTeacherAlreadyExistShouldReturnOptionalEmpty() {
        //Arrange
        Teacher teacherToAdd = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        // Act
        Optional<Teacher> teacherAddedSavedOpt = teacherService.addNewTeacher(teacherToAdd);

        //Assert
        assertTrue(teacherAddedSavedOpt.isEmpty());
    }


}