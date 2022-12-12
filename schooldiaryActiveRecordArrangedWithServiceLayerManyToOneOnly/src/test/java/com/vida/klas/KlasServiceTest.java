package com.vida.klas;

import com.vida.BasePrepForTests;
import com.vida.model.Klas;
import com.vida.model.enums.KlasLetterEnum;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
class KlasServiceTest extends BasePrepForTests {
    @Inject
    KlasService klasService;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Override
    @AfterEach
    public void tearDown() {
        super.tearDown();
    }

    @Test
    @Order(1)
    void testGetKlasById() {
        //Arrange
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        // Act
        Klas klasById = klasService.getKlasById(klas1A.getId()).orElse(null);


        //Assert
        assertTrue(klasById != null);
        assertEquals(klasById, klasService.getKlasById(klasById.getId()).get());
    }

    @Test
    @Order(2)
    void testGetAllKlassesWhenPageOneAndPageTwo() {
        // Act
        List<Klas> allKlassesFirstPage = klasService.getAllKlasses(1, 4);
        List<Klas> allKlassesSecondPage = klasService.getAllKlasses(2, 4);

        //Assert
        assertEquals(4, allKlassesFirstPage.size());
        assertEquals(1, allKlassesFirstPage.get(0).getKlasNumber());
        assertEquals("A", allKlassesFirstPage.get(0).getKlasLetterEnum().toString());

        assertEquals(4, allKlassesSecondPage.size());
        //Check 4th klas in second page
        assertEquals(allKlassesSecondPage.get(3), klasService.getKlasById(allKlassesSecondPage.get(3).getId()).get());
    }

    @Test
    @Order(3)
    void testGetAllKlassesWhenPage3SouldReturnEmptyList() {
        // Act
        List<Klas> allKlassesThirdPage = klasService.getAllKlasses(3, 4);

        //Assert
        assertEquals(0, allKlassesThirdPage.size());
    }


    @Test
    @Order(4)
    void testAddNewKlas() {
        //Arrange
        Klas klasNew = new Klas(3, KlasLetterEnum.A);

        //Act
        Optional<Klas> klasAddedSaved = klasService.addNewKlas(klasNew);

        //Assert
        assertTrue(klasAddedSaved.isPresent());
        assertEquals(klasAddedSaved.get(), klasService.getKlasById(klasAddedSaved.get().getId()).get());
    }

    @Test
    @Order(5)
    void testAddNewKlasWhenAddingExistingKlasShouldReturnOptionalEmpty() {
        //Arrange
        Klas klasExisting = new Klas(1, KlasLetterEnum.A);

        //Act
        Optional<Klas> klasOpt = klasService.addNewKlas(klasExisting);

        //Assert
        assertTrue(klasOpt.isEmpty());
    }

}