package com.vida.student;

import com.vida.klas.KlasService;
import com.vida.model.Klas;
import com.vida.model.Student;
import com.vida.model.enums.KlasLetterEnum;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/*
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
@Transactional
class StudentServiceTest {
    @Inject
    StudentService studentService;
    @Inject
    KlasService klasService;

    @Test
    @Order(1)
    void testGetAllStudentsWhenPageOneAndEmptyDatabaseShouldReturnEmptyList() {
        //Arrange and Act
        List<Student> allStudents = studentService.getAllStudents(1, 4);

        //Assert
        assertEquals(0, allStudents.size());
    }

    @Test
    @Order(2)
    void testAddNewStudent() {
        //Arrange
        Klas klas1A = new Klas(1, KlasLetterEnum.A);
        klasService.addNewKlas(klas1A);
        Student student1A_1 = new Student("Svilen", "Velikov", "testtestte").setKlas(klas1A);

        //Act
        studentService.addNewStudent(student1A_1);

        //Assert
        assertNotNull(student1A_1.getId());
        assertEquals(student1A_1, studentService.getStudentById(student1A_1.getId()).get());
    }


    @Test
    @Order(3)
    void testGetAllStudentsWhenPageOneAndPageTwo() {
        //Arrange
        Klas klas1A = klasService.getKlasById(1L).orElse(null);
        Student student1A_2 = new Student("Georgi", "Georgiev", "6666666661").setKlas(klas1A);
        Student student1A_3 = new Student("Vassil", "Levski", "vvvvvvvvvv").setKlas(klas1A);
        Student student1A_4 = new Student("Boiko", "Borisov", "bbbbbbbbbb").setKlas(klas1A);
        Student student1A_5 = new Student("Zahari", "Stoyanov", "zzzzzzzzzz").setKlas(klas1A);
        Student student1A_6 = new Student("Kiril", "Petkov", "kkkkkkkkkk").setKlas(klas1A);
        Student student1A_7 = new Student("Gospodin", "Gospodinov", "gggggggggg").setKlas(null);
        this.studentService.addNewStudent(student1A_2);
        this.studentService.addNewStudent(student1A_3);
        this.studentService.addNewStudent(student1A_4);
        this.studentService.addNewStudent(student1A_5);
        this.studentService.addNewStudent(student1A_6);
        this.studentService.addNewStudent(student1A_7);

        // Act
        List<Student> allStudentsFirstPage = studentService.getAllStudents(1, 4);
        List<Student> allStudentsSecondPage = studentService.getAllStudents(2, 4);

        //Assert
        assertEquals(4, allStudentsFirstPage.size());
        assertEquals("testtestte", allStudentsFirstPage.get(0).getEgn());
        assertEquals(1, allStudentsFirstPage.get(0).getId());

        assertEquals(3, allStudentsSecondPage.size());
        //Check 3d student in second page
        assertEquals(allStudentsSecondPage.get(2), studentService.getStudentById(allStudentsSecondPage.get(2).getId()).get());
    }


    @Test
    @Order(4)
    void testGetAllStudentsWhenPage3ShouldReturnsEmptyList() {
        //Arrange & Act
        List<Student> allStudentsThirdPage = studentService.getAllStudents(3, 4); //here page index

        //Assert
        assertEquals(0, allStudentsThirdPage.size());
    }


    @Test
    @Order(5)
    void testGetStudentById() {
        Optional<Student> studentById = studentService.getStudentById(1L);

        assertNotNull(studentById.get());
        assertEquals("testtestte", studentById.get().getEgn());
        assertEquals(1, studentById.get().getId());
        assertEquals(studentById.get(), studentService.getStudentById(studentById.get().getId()).get());
    }

    //Kогато обработвам optional винаги обработвам .isEmpty  .isPresent
    @Test
    @Order(6)
    void testGetStudentByIdWhenStudentDoesNotExistShouldReturnEmptyOptional() {
        Optional<Student> studentById = studentService.getStudentById(15L);

        assertTrue(studentById.isEmpty());

// така не!!!       assertThrows(NoSuchElementException.class, () -> studentById.get());
    }


    @Test
    @Order(7)
    void testGetAllStudentsByKlasWhenPageOneAndPageTwo() {
        //Arrange and Act
        Klas klas1A = klasService.getKlasById(1L).get();

        List<Student> allStudentsByKlasFirstPage = studentService.getAllStudentsByKlas(klas1A, 1, 4);
        List<Student> allStudentsByKlasSecondPage = studentService.getAllStudentsByKlas(klas1A, 2, 4);

        //Assert
        assertEquals(4, allStudentsByKlasFirstPage.size());
        assertEquals(2, allStudentsByKlasSecondPage.size());
    }


    @Test
    @Order(8)
    void testGetAllStudentsByKlasWhenPage3ShouldReturnEmptyList() {
        //Arrange and Act
        Klas klas1A = klasService.getKlasById(1L).get();
        List<Student> allStudentsByKlas = studentService.getAllStudentsByKlas(klas1A, 3, 4);

        //Assert
        assertEquals(0, allStudentsByKlas.size());
    }


    @Test
    @Order(9)
    void testGetAllStudentsByKlasWhenPageOneAndEmptyKlasShouldReturnEmptyList() {
        //Arrange
        Klas klas1A = klasService.getKlasById(1L).get();
        Klas klas1B = new Klas(1, KlasLetterEnum.B);
        klasService.addNewKlas(klas1B);

        //Act
        List<Student> allStudentsByKlas1B = studentService.getAllStudentsByKlas(klas1B, 1, 4);

        //Assert
        assertEquals(0, allStudentsByKlas1B.size());
    }


    @Test
    @Order(10)
    void testGetStudentByEgn() {
        //Arrange and Actst    //Assert
        final Long expectedStudentId = 1L;
        studentService.getStudentByEgn("testtestte")
                .ifPresentOrElse(st -> assertEquals(expectedStudentId, st.getId()), () -> Assertions.fail());
    }


    @Test
    @Order(11)
    void testAssignStudentToKlas() {
        //Arrange
        Klas klas1A = klasService.getKlasById(1L).get();
        Student student1A_7 = studentService.getStudentById(7L).get();


        //Act
        Student studentAssingned = studentService.assignStudentToKlas(student1A_7, klas1A);

        //Assert
        assertNotNull(studentAssingned);
        assertEquals(klas1A, studentAssingned.getKlas());
        assertEquals(studentAssingned, studentService.getStudentById(studentAssingned.getId()).get());
    }
}

 */