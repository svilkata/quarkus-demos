package com.vidasoft.diary.teacher;

import com.vidasoft.diary.BasePrep;
import com.vidasoft.diary.model.Clazz;
import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.model.Teacher;
import com.vidasoft.diary.student.StudentDTO;
import com.vidasoft.diary.student.StudentResource;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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
import static com.vidasoft.diary.teacher.TeacherResource.TEACHER_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(TeacherResource.class)
@Tag("restassured-integration")
class TeacherResourceTest extends BasePrep {

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
    void testCreateTeacher() {
        //Arrange
        // Creates a teacherDto instance
        TeacherDTO teacherDTO = new TeacherDTO("Uchitel", "Uchitelov", "1231231231");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonTeacherDto = jsonb.toJson(teacherDTO);

        //Act and Assert
        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .body(jsonTeacherDto)
                .when().post()
                .then()
                .statusCode(201);

        //Get the last added student
        List<Teacher> list = Teacher.<Teacher>findAll().list();
        Teacher lastAddedTeacher = list.get(list.size() - 1);

        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + "/" + TEACHER_RESOURCE_PATH + "/" + lastAddedTeacher.id);
    }

    @Test
    void testCreateTeacherWhenTeacherDtoNullShouldReturn400BadRequest() {
        given()
                .contentType(APPLICATION_JSON)
                .body(JsonbBuilder.create().toJson(null))
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateTeacherWhenTeacherDtoHasWrongIdentityFormatShouldReturn400BadRequest() {
        //Arrange
        // Creates a teacherDto instance
        TeacherDTO teacherDTO = new TeacherDTO("Uchitel", "Uchitelov", "asfae23123");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonTeacherDto = jsonb.toJson(teacherDTO);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonTeacherDto)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateTeacherWhenTeacherDtoHasEmptyFirstNameAndNullLastNameShouldReturn400BadRequest() {
        //Arrange
        // Creates a teacherDto instance
        TeacherDTO teacherDTO = new TeacherDTO("", null, "1231231231");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonTeacherDto = jsonb.toJson(teacherDTO);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonTeacherDto)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateTeacherWhenTeacherDtoDoesNotHaveUniqueIdentityShouldAlsoReturn400BadRequest() {
        //Arrange
        // Creates a teacherDto instance
        TeacherDTO teacherDTO = new TeacherDTO("Uchitel", "Uchitelov", EGNTEACHER_1);

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonTeacherDto = jsonb.toJson(teacherDTO);

        //Act and Assert
        given()
                .contentType(APPLICATION_JSON)
                .body(jsonTeacherDto)
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testGetAllTeachersPageOne() {
        //Arrange
        //By default, page is 1, and size is 4

        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("identity", hasItems(EGNTEACHER_1, EGNTEACHER_2, EGNTEACHER_3, EGNTEACHER_4));
    }

    @Test
    void testGetAllTeachersPageFourShouldReturnEmptyList() {
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
    void testGetTeacherByIdentity() {
        given()
                .pathParam("identity", EGNTEACHER_2)
                .when().get("/identity/{identity}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("identity", is(EGNTEACHER_2));
    }

    @Test
    void testGetTeacherByIdentityWhenIdentityIsNotValidShouldReturn400BadRequest() {
        given()
                .pathParam("identity", "dwqdwq21")
                .when().get("/identity/{identity}")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetTeacherByIdentityWhenIdentityDoesNotExistInTheDBShouldReturn404NotFound() {
        given()
                .pathParam("identity", "9876543210")
                .when().get("/identity/{identity}")
                .then()
                .statusCode(404);
    }

    @Test
    void testGetClazzesOfATeacherPageOne() {
        //Arrange
        List<Teacher> list = Teacher.<Teacher>findAll().list();
        Teacher firstAddedTeacher = list.get(0);

        given()
                .pathParam("teacherId", firstAddedTeacher.id)
                .when().get("{teacherId}/clazzes")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("clazzNumber", hasItem("6"))
                .body("subclazzInitial", hasItems("A", "C"));
    }

    @Test
    void testGetClazzesOfATeacherPageFourShouldReturnEmptyList() {
        //Arrange
        List<Teacher> list = Teacher.<Teacher>findAll().list();
        Teacher firstAddedTeacher = list.get(0);

        given()
                .queryParam("page", 4)
                .pathParam("teacherId", firstAddedTeacher.id)
                .when().get("{teacherId}/clazzes")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetClazzesOfATeacherPageOneWhenTeacherIdDoesNotExistInTheDBShouldReturnEmptyList() {
        //Arrange
        Long teacherId = 25000L;

        given()
                .queryParam("page", 1)
                .pathParam("teacherId", teacherId)
                .when().get("{teacherId}/clazzes")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }


    //We can test also if teacherId is null
    @Test
    void testGetClazzesOfATeacherPageOneWhenTeacherIdIsNegativeShouldReturn400BadRequest() {
        //Arrange
        Long teacherId = -300L;

        given()
                .queryParam("page", 1)
                .pathParam("teacherId", teacherId)
                .when().get("{teacherId}/clazzes")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetSubjectsOfATeacherPageOne() {
        //Arrange
        List<Teacher> list = Teacher.<Teacher>findAll().list();
        Teacher firstAddedTeacher = list.get(0);

        given()
                .pathParam("teacherId", firstAddedTeacher.id)
                .when().get("{teacherId}/subjects")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("name", hasItems(SUBJECTMATH_1.toLowerCase(Locale.ROOT),
                        SUBJECTLITERA_2.toLowerCase(Locale.ROOT),
                        SUBJECTSPORT_6.toLowerCase(Locale.ROOT)));
    }

    @Test
    void testGetSubjectsOfATeacherPageFourShouldReturnEmptyList() {
        //Arrange
        List<Teacher> list = Teacher.<Teacher>findAll().list();
        Teacher firstAddedTeacher = list.get(0);

        given()
                .queryParam("page", 4)
                .pathParam("teacherId", firstAddedTeacher.id)
                .when().get("{teacherId}/subjects")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }


    @Test
    void testGetSubjectsOfATeacherPageOneWhenTeacherIdDoesNotExistInTheDBShouldReturnEmptyList() {
        //Arrange
        Long teacherId = 25000L;

        given()
//                .queryParam("page", 1)
                .pathParam("teacherId", teacherId)
                .when().get("{teacherId}/subjects")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }


    //We can test also if teacherId is null
    @Test
    void testGetSubjectsOfATeacherPageOneWhenTeacherIdIsNegativeShouldReturn400BadRequest() {
        //Arrange
        Long teacherId = -300L;

        given()
//                .queryParam("page", 1)
                .pathParam("teacherId", teacherId)
                .when().get("{teacherId}/subjects")
                .then()
                .statusCode(400);
    }


    @Test
    void testGetClazzesByTeacherAndSubjectPageOne() {
        //Arrange
        Teacher firstAddedTeacher = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        assertTrue(firstAddedTeacher != null);
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();

        given()
                .pathParam("teacherId", firstAddedTeacher.id)
                .pathParam("subjectId", subjectMath.id)
                .when().get("{teacherId}/clazzes/{subjectId}")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("clazzNumber", hasItem("6"))
                .body("subclazzInitial", hasItems("A", "C"));
    }

    @Test
    void testGetClazzesByTeacherAndSubjectPageFourShouldReturnEmptyList() {
        //Arrange
        Teacher firstAddedTeacher = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        assertTrue(firstAddedTeacher != null);
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();

        given()
                .queryParam("page", 4)
                .pathParam("teacherId", firstAddedTeacher.id)
                .pathParam("subjectId", subjectMath.id)
                .when().get("{teacherId}/clazzes/{subjectId}")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetClazzesByTeacherAndSubjectWhenPageOneAndTeacherIdDoesNotExistAndSubjectIdDoesNotExistInTheDBShouldReturnEmptyList() {
        //Arrange
        Long teacherId = 25000L;
        Long subjectId = 26000L;

        given()
                .queryParam("page", 4)
                .pathParam("teacherId", teacherId)
                .pathParam("subjectId", subjectId)
                .when().get("{teacherId}/clazzes/{subjectId}")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetClazzesByTeacherAndSubjectWhenPageOneAndTeacherIdDoesNotHaveSuchSubjectToTeachInAnyClazzShouldReturnEmptyList() {
        //Arrange
        Teacher firstAddedTeacher = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        assertNotNull(firstAddedTeacher);
        Subject subjectBiology = Subject.<Subject>find("name", SUBJECTBIOL_3.toLowerCase(Locale.ROOT)).firstResult();
        assertNotNull(subjectBiology);


        given()
//                .queryParam("page", 1)
                .pathParam("teacherId", firstAddedTeacher.id)
                .pathParam("subjectId", subjectBiology.id)
                .when().get("{teacherId}/clazzes/{subjectId}")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetClazzesByTeacherAndSubjectWhenPageOneAndTeacherIdAndSubjectIdAreNegativeShouldReturn400BadRequest() {
        //Arrange
        Long teacherId = -300L;
        Long subjectId = -301L;

        given()
                .queryParam("page", 4)
                .pathParam("teacherId", teacherId)
                .pathParam("subjectId", subjectId)
                .when().get("/{teacherId}/clazzes/{subjectId}")
                .then()
                .statusCode(400);
    }


    @Test
    void testGetSubjectsByTeacherAndClazzPageOne() {
        //Arrange
        Teacher firstAddedTeacher = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        assertTrue(firstAddedTeacher != null);
        Clazz clazz_6A = Clazz.<Clazz>findAll().list().get(0);

        given()
                .pathParam("teacherId", firstAddedTeacher.id)
                .pathParam("clazzId", clazz_6A.id)
                .when().get("/{teacherId}/subjects/{clazzId}")
                .then()
                .statusCode(200)
                .body("size()", is(3))
                .body("name", hasItems(SUBJECTMATH_1.toLowerCase(Locale.ROOT),
                        SUBJECTLITERA_2.toLowerCase(Locale.ROOT),
                        SUBJECTSPORT_6.toLowerCase(Locale.ROOT)));
    }

    @Test
    void testGetSubjectsByTeacherAndClazzPageFourShouldReturnEmptyList() {
        //Arrange
        Teacher firstAddedTeacher = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        assertTrue(firstAddedTeacher != null);
        Clazz clazz_6A = Clazz.<Clazz>findAll().list().get(0);

        given()
                .queryParam("page", 4)
                .pathParam("teacherId", firstAddedTeacher.id)
                .pathParam("clazzId", clazz_6A.id)
                .when().get("/{teacherId}/subjects/{clazzId}")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetSubjectsByTeacherAndClazzWhenPageOneAndTeacherIdDoesNotExistAndClazzIdDoesNotExistInTheDBShouldReturnEmptyList() {
        //Arrange
        Long teacherId = 25000L;
        Long clazzId = 26000L;

        given()
//                .queryParam("page", 1)
                .pathParam("teacherId", teacherId)
                .pathParam("clazzId", clazzId)
                .when().get("/{teacherId}/subjects/{clazzId}")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetSubjectsByTeacherAndClazzWhenPageOneAndTeacherIdDoesNotHaveSuchClazzToTeachOnAnySubjectShouldReturnEmptyList() {
        //Arrange
        Teacher firstAddedTeacher = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        assertTrue(firstAddedTeacher != null);
        Clazz clazz_6B = Clazz.<Clazz>findAll().list().get(1);


        given()
//                .queryParam("page", 1)
                .pathParam("teacherId", firstAddedTeacher.id)
                .pathParam("clazzId", clazz_6B.id)
                .when().get("/{teacherId}/subjects/{clazzId}")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }


    @Test
    void testGetSubjectsByTeacherAndClazzWhenPageOneAndTeacherIdAndClazzIdAreNegativeShouldReturn400BadRequest() {
        //Arrange
        Long teacherId = -300L;
        Long clazzId = -301L;

        given()
//                .queryParam("page", 1)
                .pathParam("teacherId", teacherId)
                .pathParam("clazzId", clazzId)
                .when().get("/{teacherId}/subjects/{clazzId}")
                .then()
                .statusCode(400);
    }

}