package com.vida.subject;

import com.vida.BasePrepForTests;
import com.vida.model.Subject;
import com.vida.subject.dto.SubjectDto;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.List;

import static com.vida.subject.SubjectRestResource.SUBJECT_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(SubjectRestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("restassured-integration")
class SubjectRestResourceTest extends BasePrepForTests {
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
        Subject subject_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        given()
                .pathParam("subjectId", subject_1.getId())
                .when().get("/{subjectId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(2))
                .body("subject", is(SUBJECTMATH_1));
    }

    @Test
    @Order(2)
    void testGetSubjectByIdWhenSubjectIdDoesNotExistShouldReturn404NotFound() {
        given()
                .pathParam("subjectId", 2500)
                .when().get("/{subjectId}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("No subject with id"));
    }

    @Test
    @Order(3)
    void testGetSubjectByIdWhenSubjectIdIsNegativeShouldReturn400BadRequest() {
        given()
                .pathParam("subjectId", -300)
                .when().get("/{subjectId}")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    void testAddNewSubject() {
        // Creates a subjectDto instance
        SubjectDto subjectDto = new SubjectDto("INFORMATIQUES");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonSubjectDto = jsonb.toJson(subjectDto);

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .body(jsonSubjectDto)
                .when().post()
                .then()
                .statusCode(200)
                .body("subject", is(subjectDto.getSubject()));

        List<Subject> list = Subject.<Subject>findAll().list();

        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + SUBJECT_RESOURCE_PATH + "/" + list.get(list.size()-1).getId());
    }

    @Test
    @Order(5)
    void testAddNewSubjectWhenSubjectAlreadyExistsShouldReturn409Conflict() {
        // Creates a klasDto instance
        SubjectDto subjectDto = new SubjectDto(SUBJECTMATH_1);

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonSubjectDto = jsonb.toJson(subjectDto);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonSubjectDto)
                .when().post()
                .then()
                .statusCode(409)
                .body("Comment", anything("already exists in the database"));
    }

    //!!!We can test here also some of the validated fields of the SubjectDto
    @Test
    @Order(6)
    void testAddNewSubjectWhenSubjectDtoNullShouldReturn400BadRequest() {
        given()
                .contentType(APPLICATION_JSON)
                .body(JsonbBuilder.create().toJson(null))
                .when().post()
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    void testGetAllSubjectsPageOne() {
        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("subject", hasItems(SUBJECTMATH_1, SUBJECTLITERA_2, SUBJECTBIOL_3, SUBJECTGEOGRA_4));

    }

    @Test
    @Order(8)
    void testGetAllSubjectsPageFourShouldReturnEmptyList() {
        given()
                .queryParam("page", 4)
                .queryParam("size", 4)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }
}