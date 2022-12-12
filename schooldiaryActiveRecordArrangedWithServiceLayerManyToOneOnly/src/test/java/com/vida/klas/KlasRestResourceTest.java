package com.vida.klas;

import com.vida.BasePrepForTests;
import com.vida.klas.dto.KlasDto;
import com.vida.model.Klas;
import com.vida.model.Student;
import com.vida.student.StudentService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.transaction.Transactional;
import java.util.List;

import static com.vida.klas.KlasRestResource.KLAS_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.*;


@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(KlasRestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("restassured-integration")
@Transactional
class KlasRestResourceTest extends BasePrepForTests {
    @Inject
    KlasService klasService;
    @Inject
    StudentService studentService;

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
    void testGetKlassById() {
        //Arrange
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .pathParam("id", klas1A.getId())
                .when().get("/{id}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(3))
                .body("klasNumber", is(1))
                .body("klasLetter", is("A"));
    }

    @Test
    @Order(2)
    void testGetKlassByIdWhenKlasIdDoesNotExistShouldReturn404NotFound() {
        given()
                .pathParam("id", 2500)
                .when().get("/{id}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("No klas with id"));
    }

    @Test
    @Order(3)
    void testGetKlassByIdWhenKlasIdIsNegativeShouldReturn400BadRequest() {
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("id", -300)
                .when().get("/{id}")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    void testAddNewKlas() {
        // Creates a klasDto instance
        KlasDto klasDto = new KlasDto().setKlasNumber(3L).setKlasLetter("A");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonKlas = jsonb.toJson(klasDto);

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .body(jsonKlas)
                .when().post()
                .then()
                .statusCode(200)
                .body("klasNumber", is(3))
                .body("klasLetter", is("A"));

        List<Klas> list = Klas.<Klas>findAll().list();

        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + KLAS_RESOURCE_PATH + "/" + list.get(list.size()-1).getId());
    }

    @Test
    @Order(5)
    void testAddNewKlasWhenKlasAlreadyExistsShouldReturn409Conflict() {
        // Creates a klasDto instance
        KlasDto klasDto = new KlasDto().setKlasNumber(1L).setKlasLetter("A");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonKlas = jsonb.toJson(klasDto);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonKlas)
                .when().post()
                .then()
                .statusCode(409)
                .body("Comment", anything("already exists in the database"));
    }

    //This test is equal to klasDto да е {}  null
    @Test
    @Order(6)
    void testAddNewKlasWhenKlasNegativeKlasNumberOrWrongDataShouldReturn400BadRequest() {
        // Creates a klasDto instance
        KlasDto klasDto = new KlasDto().setKlasNumber(-1L).setKlasLetter("E");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonKlas = jsonb.toJson(klasDto);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonKlas)
                .when().post()
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    void testAssignKlasAStudent() {
        Student studentByEgn = studentService.getStudentByEgn(EGNSTUDENT1A_7).orElse(null);
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .pathParam("klasId", klas1A.getId())
                .pathParam("studentId", studentByEgn.getId())
                .contentType(APPLICATION_JSON)
                .when().patch("/{klasId}/student/{studentId}")
                .then()
                .statusCode(200)
                .body("size()", is(6))
                .body("id", is(Integer.parseInt(studentByEgn.getId() + "")))    //studentId
                .body("klasId", is(Integer.parseInt(klas1A.getId() + "")));
    }

    @Test
    @Order(8)
    void testAssignKlasAStudentWhenStudentAlreadyHasAKlasShouldReturn409Conflict() {
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);
        Student student1A_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);

        given()
                .pathParam("klasId", klas1A.getId())
                .pathParam("studentId", student1A_1.getId())
                .contentType(APPLICATION_JSON)
                .when().patch("/{klasId}/student/{studentId}")
                .then()
                .statusCode(409)
                .body("size()", is(1))
                .body("Conflict", anything("there is already assigned student with id"));
    }

    @Test
    @Order(9)
    void testAssignKlasAStudentWhenStudentIdAndKlasIdNotPresentShouldReturn404NotFound() {
        given()
                .pathParam("klasId", 2500)
                .pathParam("studentId", 2500)
                .contentType(APPLICATION_JSON)
                .when().patch("/{klasId}/student/{studentId}")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(10)
    void testAssignKlasAStudentWhenStudentIdAndKlasIdNegativeShouldReturn400BadRequest() {
        given()
                .pathParam("klasId", -5)
                .pathParam("studentId", -15)
                .contentType(APPLICATION_JSON)
                .when().patch("/{klasId}/student/{studentId}")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(11)
    void testGetAllKlassesWhenPageOne() {
        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("klasNumber", hasItems(1))
                .body("klasLetter", hasItems("A", "B", "C", "D"));
    }

    @Test
    @Order(12)
    void testGetAllKlassesWhenPageFourShouldReturnEmptyList() {
        given()
                .queryParam("page", 4)
                .queryParam("size", 4)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }


    @Test
    @Order(13)
    void testGetAllKlasStudentsByKlasIdPageOne() {
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .pathParam("klasId", klas1A.getId())
                .contentType(APPLICATION_JSON)
                .when().get("/{klasId}/students")
                .then()
                .statusCode(200)
                .body("size()", is(4))
                .body("egn", hasItems(EGNSTUDENT1A_1, EGNSTUDENT1A_2, EGNSTUDENT1A_3, EGNSTUDENT1A_4));
    }

    @Test
    @Order(14)
    void testGetAllKlasStudentsByKlasIdPageFourShouldReturnEmptyList() {
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .queryParam("page", 4)
                .queryParam("size", 4)
                .pathParam("klasId", klas1A.getId())
                .contentType(APPLICATION_JSON)
                .when().get("/{klasId}/students")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    @Order(15)
    void testGetAllKlasStudentsByKlasIdWhenKlasIdDoesNotExistShouldReturn404NotFound() {
        //Arrange, Act and assert
        given()
                .queryParam("page", 1)
                .queryParam("size", 4)
                .pathParam("klasId", 2500)
                .contentType(APPLICATION_JSON)
                .when().get("/{klasId}/students")
                .then()
                .statusCode(404)
                .body("Comment", anything("No klas with id"));
    }

    @Test
    @Order(16)
    void testGetAllKlasStudentsByKlasIdWhenKlasIdIsNegativeShouldReturn400BadRequest() {
        //Arrange, Act and assert
        given()
                .queryParam("page", 1)
                .queryParam("size", 4)
                .pathParam("klasId", -300)
                .contentType(APPLICATION_JSON)
                .when().get("/{klasId}/students")
                .then()
                .statusCode(400);
    }


    @Test
    @Order(17)
    void testGetAllKlasTeachersByKlasIdPageOne() {
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .queryParam("page", 1)
                .queryParam("size", 4)
                .pathParam("klasId", klas1A.getId())
                .contentType(APPLICATION_JSON)
                .when().get("/{klasId}/teachers")
                .then()
                .statusCode(200)
                .body("size()", is(4))
                .body("egn", hasItems(EGNTEACHER_1, EGNTEACHER_3));
    }

    @Test
    @Order(18)
    void testGetAllKlasTeachersByKlasIdPageFourShouldReturnEmptyList() {
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .queryParam("page", 4)
                .queryParam("size", 4)
                .pathParam("klasId", klas1A.getId())
                .contentType(APPLICATION_JSON)
                .when().get("/{klasId}/teachers")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    @Order(19)
    void testGetAllKlasTeachersByKlasIdWhenKlasIdDoesNotExistShouldReturn404NotFound() {
        given()
                .queryParam("page", 4)
                .queryParam("size", 4)
                .pathParam("klasId", 2500)
                .contentType(APPLICATION_JSON)
                .when().get("/{klasId}/teachers")
                .then()
                .statusCode(404)
                .body("Comment", anything("No klas with id"));
    }

    @Test
    @Order(20)
    void testGetAllKlasTeachersByKlasIdWhenKlasIdIsNegativeShouldReturn400BadRequest() {
        given()
                .queryParam("page", 1)
                .queryParam("size", 4)
                .pathParam("klasId", -300)
                .contentType(APPLICATION_JSON)
                .when().get("/{klasId}/teachers")
                .then()
                .statusCode(400);
    }
}