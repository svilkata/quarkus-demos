package com.vidasoft.diary.subject;

import com.vidasoft.diary.BasePrep;
import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.student.StudentDTO;
import com.vidasoft.diary.student.StudentResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.List;
import java.util.Locale;

import static com.vidasoft.diary.student.StudentResource.STUDENT_RESOURCE_PATH;
import static com.vidasoft.diary.subject.SubjectResource.SUBJECT_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(SubjectResource.class)
@Tag("restassured-integration")
class SubjectResourceTest extends BasePrep {
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
    void testCreateSubject() {
        //Arrange
        // Creates a subjectDto instance
        SubjectDTO subjectDTO = new SubjectDTO("ezikoznanie");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonSubjectDto = jsonb.toJson(subjectDTO);

        //Act and Assert
        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .body(jsonSubjectDto)
                .when().post()
                .then()
                .statusCode(201);

        //Get the last added student
        List<Subject> list = Subject.<Subject>findAll().list();
        Subject lastAddedSubject = list.get(list.size() - 1);

        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + "/" + SUBJECT_RESOURCE_PATH + "/" + lastAddedSubject.id);

    }

    @Test
    void testCreateSubjectWhenSubjectDtoNullShouldReturn400BadRequest() {
        given()
                .contentType(APPLICATION_JSON)
                .body(JsonbBuilder.create().toJson(null))
                .when()
                .post()
                .then()
                .statusCode(400);
    }


    @Test
    void testCreateSubjectWhenSubjectDtoHasFieldSubjectWhichIsLessThan4SymbolsShouldReturn400BadRequest() {
        //Arrange
        // Creates a subjectDto instance
        SubjectDTO subjectDTO = new SubjectDTO("ez");

        //We can test also these scenarios which will again return 400BadRequest
//        SubjectDTO subjectDTO = new SubjectDTO("");
//        SubjectDTO subjectDTO = new SubjectDTO(null);

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonSubjectDto = jsonb.toJson(subjectDTO);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonSubjectDto)
                .when()
                .post()
                .then()
                .statusCode(400);
    }


    @Test
    void testCreateSubjectWhenSubjectDtoDoesNotHaveUniqueSubjectShouldReturn400BadRequest() {
        //Arrange
        // Creates a subjectDto instance
        SubjectDTO subjectDTO = new SubjectDTO(SUBJECTMATH_1);

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonSubjectDto = jsonb.toJson(subjectDTO);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonSubjectDto)
                .when()
                .post()
                .then()
                .statusCode(400);
    }


    @Test
    void testGetAllSubjectsPageOne() {
        //Arrange
        //By default, page is 1, and size is 4

        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("name", hasItems(SUBJECTMATH_1.toLowerCase(Locale.ROOT),
                        SUBJECTLITERA_2.toLowerCase(Locale.ROOT),
                        SUBJECTBIOL_3.toLowerCase(Locale.ROOT),
                        SUBJECTGEOGRA_4.toLowerCase(Locale.ROOT)));
    }

    @Test
    void testGetAllSubjectsPageFourShouldReturnEmptyList() {
        //Arrange

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
    void testGetSubjectByName() {
        given()
                .pathParam("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT))
                .when().get("/name/{name}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(2))
                .body("name", is(SUBJECTMATH_1.toLowerCase(Locale.ROOT)));
    }

    @Test
    void testGetSubjectByNameWhenNameIsNotValidShouldReturn400BadRequest() {
        given()
                .pathParam("name", "a")
                .when().get("/name/{name}")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetSubjectByNameWhenNameDoesNotExistinTheDBShouldReturn404NotFound() {
        given()
                .pathParam("name", "Martial arts")
                .when().get("/name/{name}")
                .then()
                .statusCode(404);
    }
}