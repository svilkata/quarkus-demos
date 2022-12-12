package com.vidasoft.diary.student;

import com.vidasoft.diary.BasePrep;
import com.vidasoft.diary.model.Student;
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

import static com.vidasoft.diary.student.StudentResource.STUDENT_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
@TestHTTPEndpoint(StudentResource.class)
@Tag("restassured-integration")
class StudentResourceTest extends BasePrep {
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
    void testCreateStudent() {
        //Arrange
        // Creates a studentDto instance
        StudentDTO studentDTO = new StudentDTO("Iskren", "Petsov", "1231231231");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonStudentDto = jsonb.toJson(studentDTO);

        //Act and Assert
        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .body(jsonStudentDto)
                .when().post()
                .then()
                .statusCode(201);

        //Get the last added student
        List<Student> list = Student.<Student>findAll().list();
        Student lastAddedStudent = list.get(list.size() - 1);

        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + "/" + STUDENT_RESOURCE_PATH + "/" + lastAddedStudent.id);
    }


    @Test
    void testCreateStudentWhenStudentDtoNullShouldReturn400BadRequest() {
        given()
                .contentType(APPLICATION_JSON)
                .body(JsonbBuilder.create().toJson(null))
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateStudentWhenStudentDtoHasWrongIdentityFormatShouldReturn400BadRequest() {
        //Arrange
        // Creates a studentDto instance
        StudentDTO studentDTO = new StudentDTO("Iskren", "Petsov", "asfae23123");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonStudentDto = jsonb.toJson(studentDTO);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonStudentDto)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateStudentWhenStudentDtoHasEmptyFirstNameAndNullLastNameShouldReturn400BadRequest() {
        //Arrange
        // Creates a studentDto instance
        StudentDTO studentDTO = new StudentDTO("", null, "1231231231");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonStudentDto = jsonb.toJson(studentDTO);

        given()
                .contentType(APPLICATION_JSON)
                .body(JsonbBuilder.create().toJson(null))
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateStudentWhenStudentDtoDoesNotHaveUniqueIdentityShouldAlsoReturn400BadRequest() {
        //Arrange
        // Creates a studentDto instance
        StudentDTO studentDTO = new StudentDTO("Iskren", "Petsov", EGNSTUDENT1A_1);

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonStudentDto = jsonb.toJson(studentDTO);

        //Act and Assert
        given()
                .contentType(APPLICATION_JSON)
                .body(jsonStudentDto)
                .when()
                .post()
                .then()
                .statusCode(400);
    }


    @Test
    void testGetAllStudentsPageOne() {
        //Arrange
        //By default, page is 1, and size is 4

        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("identity", hasItems(EGNSTUDENT1A_1, EGNSTUDENT1A_2, EGNSTUDENT1A_3, EGNSTUDENT1A_4));
    }

    @Test
    void testGetAllStudentsPageFourShouldReturnEmptyList() {
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
    void testGetStudentByIdentity() {
        given()
                .pathParam("identity", EGNSTUDENT1A_1)
                .when().get("/identity/{identity}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(5))
                .body("identity", is(EGNSTUDENT1A_1))
                .body("clazz", is("6A"));
    }

    @Test
    void testGetStudentByIdentityWhenIdentityIsNotValidShouldReturn400BadRequest() {
        given()
                .pathParam("identity", "dwqdwq21")
                .when().get("/identity/{identity}")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetStudentByIdentityWhenIdentityDoesNotExistInTheDBShouldReturn404NotFound() {
        given()
                .pathParam("identity", "9876543210")
                .when().get("/identity/{identity}")
                .then()
                .statusCode(404);
    }

}