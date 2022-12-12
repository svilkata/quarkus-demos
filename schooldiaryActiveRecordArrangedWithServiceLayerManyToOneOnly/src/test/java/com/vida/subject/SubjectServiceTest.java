package com.vida.subject;

import com.vida.BasePrepForTests;
import com.vida.model.Klas;
import com.vida.model.Subject;
import com.vida.model.enums.KlasLetterEnum;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
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
class SubjectServiceTest extends BasePrepForTests {
    @Inject
    SubjectService subjectService;

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
    void testGetSubjectById() {
        //Arrange & Act
        Subject subject_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Subject subjectById = subjectService.getSubjectById(subject_1.getId()).orElse(null);

        //Assert
        assertTrue(subjectById != null);
        assertEquals(subjectById, subjectService.getSubjectById(subjectById.getId()).get());
    }

    @Test
    @Order(2)
    void testGetAllSubjectsWhenPageOneAndPageTwo() {
        // Act
        List<Subject> allSubjectsFirstPage = subjectService.getAllSubjects(1, 4);
        List<Subject> allSubjectsSecondPage = subjectService.getAllSubjects(2, 4);

        //Assert
        assertEquals(4, allSubjectsFirstPage.size());
        assertEquals(SUBJECTMATH_1, allSubjectsFirstPage.get(0).getSubject());

        assertEquals(2, allSubjectsSecondPage.size());
        //Check 6th subject in second page
        assertEquals(allSubjectsSecondPage.get(1), subjectService.getSubjectById(allSubjectsSecondPage.get(1).getId()).get());
    }

    @Test
    @Order(3)
    void testGetAllSubjectsWhenPageFourShouldReturnEmptyList() {
        // Act
        List<Subject> allSubjectsFourthPage = subjectService.getAllSubjects(4, 4);

        //Assert
        assertEquals(0, allSubjectsFourthPage.size());
    }

    @Test
    @Order(4)
    void testAddNewSubject() {
        //Arrange
        Subject subjectNew = new Subject("Svobodoznanie");

        //Act
        Subject subjectAddedSaved = subjectService.addNewSubject(subjectNew);

        //Assert
        assertTrue(subjectAddedSaved != null);
        assertEquals(subjectAddedSaved, subjectService.getSubjectById(subjectAddedSaved.getId()).get());
    }

    @Test
    @Order(5)
    void testGetSubjectBySubjectString() {
        Optional<Subject> subjectBySubjectStringOpt = subjectService.getSubjectBySubjectString(SUBJECTMATH_1);

        assertTrue(subjectBySubjectStringOpt.isPresent());
        assertEquals(SUBJECTMATH_1, subjectBySubjectStringOpt.get().getSubject());
    }

    @Test
    @Order(6)
    void testGetSubjectBySubjectStringWhenSubjectStringIsMissingShouldReturnOptionalEmpty() {
        Optional<Subject> subjectBySubjectStringOpt = subjectService.getSubjectBySubjectString("dwqdwqd");

        assertTrue(subjectBySubjectStringOpt.isEmpty());
    }
}