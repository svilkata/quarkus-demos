package com.vidasoft.diary.clazz;

import com.vidasoft.diary.BasePrep;
import com.vidasoft.diary.model.Clazz;
import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.model.Teacher;
import com.vidasoft.diary.teacher.TeacherDTO;
import com.vidasoft.diary.teacher.TeacherResource;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import java.util.List;
import java.util.Locale;

import static com.vidasoft.diary.clazz.ClazzResource.CLAZZ_RESOURCE_PATH;
import static com.vidasoft.diary.teacher.TeacherResource.TEACHER_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(ClazzResource.class)
@Tag("restassured-integration")
class ClazzResourceTest extends BasePrep {

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
    void testCreateClazz() {
        //Arrange
        JsonObject clazzDtoJsonObject = Json.createObjectBuilder()
                .add("clazzNumber", "10")
                .add("subclazzInitial", "A")
                .build();

        //Act and Assert
        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .body(clazzDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(201);

        //Get the last added student
        List<Clazz> list = Clazz.<Clazz>findAll().list();
        Clazz lastAddedClazz = list.get(list.size() - 1);

        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + "/" + CLAZZ_RESOURCE_PATH + "/" + lastAddedClazz.id);
    }

    @Test
    void testCreateClazzWhenClazzDtoNullShouldReturn400BadRequest() {
        given()
                .contentType(APPLICATION_JSON)
                .body(JsonbBuilder.create().toJson(null))
                .when()
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void testCreateClazzWhenClazzDtoHasWrongclazzNumberAndWrongSubclazzInitialShouldReturn400BadRequest() {
        //Arrange
        JsonObject clazzDtoJsonObject = Json.createObjectBuilder()
                .add("clazzNumber", "13")
                .add("subclazzInitial", "E")
                .build();

        given()
                .contentType(APPLICATION_JSON)
                .body(clazzDtoJsonObject.toString())
                .when()
                .post()
                .then()
                .statusCode(400);
    }


    @Test
    void testCreateClazzWhenClazzDtoDoesNotHaveUniqueClazzNumberAndSubclazzInitialThenShouldReturn400BadRequest() {
        //Arrange
        JsonObject clazzDtoJsonObject = Json.createObjectBuilder()
                .add("clazzNumber", "6")
                .add("subclazzInitial", "A")
                .build();

        given()
                .contentType(APPLICATION_JSON)
                .body(clazzDtoJsonObject.toString())
                .when()
                .post()
                .then()
                .statusCode(400);
    }


    @Test
    void testGetAllClazzesPageOne() {
        //Arrange
        //By default, page is 1, and size is 4

        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("clazzNumber", hasItem("6"))
                .body("subclazzInitial", hasItems("A", "B", "C", "D"));
    }

    @Test
    void testGetAllClazzesPageEightShouldReturnEmptyList() {
        //Arrange
        //By default, page is 1, and size is 4

        given()
                .queryParam("page", 8)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }


    @Test
    void testGetClazzByName() {
        given()
                .pathParam("clazzName", "6A")
                .when().get("name/{clazzName}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .log().all()
                .body("size()", is(3))
                .body("clazzNumber", is("6"))
                .body("subclazzInitial", is("A"));
    }

    @Test
    void testGetClazzByNameWhenClazzNameIsNotValidShouldReturn400BadRequest() {
        given()
                .pathParam("clazzName", "66A")
                .when().get("name/{clazzName}")
                .then()
                .statusCode(400)
                .contentType(APPLICATION_JSON);
    }

    @Test
    void testGetClazzByNameWhenClazzNameDoesNotExistInTheDBShouldReturn404NotFound() {
        given()
                .pathParam("clazzName", "12A")
                .when().get("name/{clazzName}")
                .then()
                .statusCode(404);
    }


    @Test
    void testAddStudentToClazz() {
        //Arrange
        Student student_7_withNoClazzYet = Student.<Student>find("identity", EGNSTUDENT1A_7).firstResult();
        List<Clazz> list = Clazz.<Clazz>findAll().list();
        Clazz clazz_6A = list.get(0);

        given()
                .pathParam("clazzId", clazz_6A.id)
                .queryParam("studentId", student_7_withNoClazzYet.id)
                .when().patch("/{clazzId}/student")
                .then()
                .statusCode(204);
    }

    @Test
    void testAddStudentToClazzWhenStudentIdAndClazzIdDoNotExistShouldReturn404NotFound() {
        //Arrange
        Long clazzId = 25000L;
        Long studentId = 25100L;


        given()
                .pathParam("clazzId", clazzId)
                .queryParam("studentId", studentId)
                .when().patch("/{clazzId}/student")
                .then()
                .statusCode(404);
    }

    @Test
    void testAddStudentToClazzWhenStudentHasAClazzAlreadyShouldReturn409Conflict() {
        //Arrange
        Student student_1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        List<Clazz> list = Clazz.<Clazz>findAll().list();
        Clazz clazz_6A = list.get(0);


        given()
                .pathParam("clazzId", clazz_6A.id)
                .queryParam("studentId", student_1.id)
                .when().patch("/{clazzId}/student")
                .then()
                .statusCode(409)
                .contentType(ContentType.JSON);
    }

    @Test
    void testAddStudentToClazzWhenStudentIdAndClazzIdAreNegativeShouldReturn400BadRequest() {
        //Arrange
        Long clazzId = -300L;
        Long studentId = -301L;


        given()
                .pathParam("clazzId", clazzId)
                .queryParam("studentId", studentId)
                .when().patch("/{clazzId}/student")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetClazzesByTeacherPageOne() {
        Teacher teacher = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        assertNotNull(teacher);

        given()
                .pathParam("teacherId", teacher.id)
                .when().get("/teacher/{teacherId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(2))
                .body("clazzNumber", hasItem("6"))
                .body("subclazzInitial", hasItems("A", "C"));
    }

    @Test
    void testGetClazzesByTeacherPageFourShouldReturnEmptyList() {
        Teacher teacher = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        assertNotNull(teacher);

        given()
                .queryParam("page", 4)
                .pathParam("teacherId", teacher.id)
                .when().get("/teacher/{teacherId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }

    @Test
    void testGetClazzesByTeacherWhenPageOneAndTeacherIdDoesNotExistInTheDBYetShouldReturnEmptyList() {
        //Assert
        Long teacherIdNotExisting = 25000L;

        given()
//                .queryParam("page", 1)
                .pathParam("teacherId", teacherIdNotExisting)
                .when().get("/teacher/{teacherId}")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void testGetClazzesByTeacherWhenPageOneAndTeacherIdIsnegativeShouldReturn400BadRequest() {
        //Assert
        Long teacherIdNegative = -300L;

        given()
//                .queryParam("page", 1)
                .pathParam("teacherId", teacherIdNegative)
                .when().get("/teacher/{teacherId}")
                .then()
                .statusCode(400);
    }


    @Test
    void testAssignSubjectToClazzAllowed() {
        //Arrange
        List<Clazz> list = Clazz.<Clazz>findAll().list();
        Clazz clazz_6A = list.get(0);
        Subject subject_history = Subject.<Subject>find("name", SUBJECTHISTORY_5.toLowerCase(Locale.ROOT)).firstResult();

        given()
                .pathParam("clazzId", clazz_6A.id)
                .queryParam("subjectId", subject_history.id)
                .when().patch("{clazzId}/allowedsubjects")
                .then()
                .statusCode(204);
    }

    @Test
    void testAssignSubjectToClazzAllowedWhenSubjectIdAndClazzIdDoNotExistShouldReturn404NotFound() {
        //Arrange
        Long clazzId = 25000L;
        Long subjectId = 25600L;

        given()
                .pathParam("clazzId", clazzId)
                .queryParam("subjectId", subjectId)
                .when().patch("{clazzId}/allowedsubjects")
                .then()
                .statusCode(404);
    }

    @Test
    void testAssignSubjectToClazzAllowedWhenSubjectIdAndClazzIdAreNegativeShouldReturn400BadRequest() {
        //Arrange
        Long clazzId = -300L;
        Long subjectId = -301L;

        given()
                .pathParam("clazzId", clazzId)
                .queryParam("subjectId", subjectId)
                .when().patch("{clazzId}/allowedsubjects")
                .then()
                .statusCode(400);
    }

    @Test
    void testAssignTeacherToClazzAndSubject() {
        //Arrange
        Clazz clazz_6B = Clazz.<Clazz>findAll().list().get(1);
        Teacher teacher = Teacher.<Teacher>find("identity", EGNTEACHER_4).firstResult();
        Subject subject_math = Subject.<Subject>find("name", SUBJECTBIOL_3.toLowerCase(Locale.ROOT)).firstResult();

        given()
                .pathParam("clazzId", clazz_6B.id)
                .queryParam("teacherId", teacher.id)
                .queryParam("subjectId", subject_math.id)
                .when().patch("{clazzId}/assign")
                .then()
                .statusCode(204);
    }

    @Test
    void testAssignTeacherToClazzAndSubjectWhenSubjectNotAllowedToBeTaughtToThisClazzShouldReturn409Conflict() {
        //Arrange
        Clazz clazz_6D = Clazz.<Clazz>findAll().list().get(3);
        Teacher teacher = Teacher.<Teacher>find("identity", EGNTEACHER_4).firstResult();
        Subject subject_math = Subject.<Subject>find("name", SUBJECTBIOL_3.toLowerCase(Locale.ROOT)).firstResult();

        given()
                .pathParam("clazzId", clazz_6D.id)
                .queryParam("teacherId", teacher.id)
                .queryParam("subjectId", subject_math.id)
                .when().patch("{clazzId}/assign")
                .then()
                .statusCode(409)
                .body("Conflict", anything("is not allowed to be taught in clazz with id"));
    }

    @Test
    void testAssignTeacherToClazzAndSubjectWhenClazzIdAndTeacherIdAndSubjectIdAreNotPresentInTheDBShouldReturn404NotFound() {
        //Arrange
        Long clazzId = 25000L;
        Long teacherId = 25000L;
        Long subjectId = 25000L;

        given()
                .pathParam("clazzId", clazzId)
                .queryParam("teacherId", teacherId)
                .queryParam("subjectId", subjectId)
                .when().patch("{clazzId}/assign")
                .then()
                .statusCode(404);
    }

    @Test
    void testAssignTeacherToClazzAndSubjectWhenClazzIdAndTeacherIdAndSubjectIdAreNegativeShouldReturn400BadRequest() {
        //Arrange
        Long clazzId = -300L;
        Long teacherId = -300L;
        Long subjectId = -300L;

        given()
                .pathParam("clazzId", clazzId)
                .queryParam("teacherId", teacherId)
                .queryParam("subjectId", subjectId)
                .when().patch("{clazzId}/assign")
                .then()
                .statusCode(400);
    }

}