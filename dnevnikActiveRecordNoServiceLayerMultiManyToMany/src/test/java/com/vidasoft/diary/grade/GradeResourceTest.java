package com.vidasoft.diary.grade;

import com.vidasoft.diary.BasePrep;
import com.vidasoft.diary.model.Grade;
import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.model.Teacher;
import com.vidasoft.diary.teacher.TeacherResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.List;
import java.util.Locale;

import static com.vidasoft.diary.grade.GradeResource.GRADE_RESOURCE_PATH;
import static com.vidasoft.diary.teacher.TeacherResource.TEACHER_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestHTTPEndpoint(GradeResource.class)
@Tag("restassured-integration")
class GradeResourceTest extends BasePrep {
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
    void testTeacherWritesGradePerStudentPerSubject() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();
        Teacher teacher1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        JsonObject gradeDtoJsonObject = Json.createObjectBuilder()
                .add("grade", 4)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .queryParam("teacherId", teacher1.id)
                .body(gradeDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(201);


        //Get the last added student
        List<Grade> list = Grade.<Grade>findAll().list();
        Grade lastAddedGrade = list.get(list.size() - 1);

        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + "/" + GRADE_RESOURCE_PATH + "/" + lastAddedGrade.id);
    }

    @Test
    void testTeacherWritesGradePerStudentPerSubjectWhenNegativeIdsShouldReturn400BadRequest() {
        //Arrange
        Long studentId = -300L;
        Long subjectId = -300L;
        Long teacherId = -300L;
        JsonObject gradeDtoJsonObject = Json.createObjectBuilder()
                .add("grade", 4)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", studentId)
                .queryParam("subjectId", subjectId)
                .queryParam("teacherId", teacherId)
                .body(gradeDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(400);
    }

    @Test
    void testTeacherWritesGradePerStudentPerSubjectWhenGradeDtoHasWrongGradeFieldShouldReturn400BadRequest() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();
        Teacher teacher1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        JsonObject gradeDtoJsonObject = Json.createObjectBuilder()
                .add("grade", 7)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .queryParam("teacherId", teacher1.id)
                .body(gradeDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(400);
    }

    @Test
    void testTeacherWritesGradePerStudentPerSubjectWhenIdsDoNotExistShouldReturn404NotFound() {
        //Arrange
        Long studentId = 25000L;
        Long subjectId = 25000L;
        Long teacherId = 25000L;
        JsonObject gradeDtoJsonObject = Json.createObjectBuilder()
                .add("grade", 5)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .queryParam("studentId", studentId)
                .queryParam("subjectId", subjectId)
                .queryParam("teacherId", teacherId)
                .body(gradeDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(404);
    }

    @Test
    void testTeacherWritesGradePerStudentPerSubjectWhenStudentDoesNotHaveClazzShouldReturn404NotFound() {
        //Arrange
        Student student7 = Student.<Student>find("identity", EGNSTUDENT1A_7).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();
        Teacher teacher1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        JsonObject gradeDtoJsonObject = Json.createObjectBuilder()
                .add("grade", 3)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .queryParam("studentId", student7.id)
                .queryParam("subjectId", subjectMath.id)
                .queryParam("teacherId", teacher1.id)
                .body(gradeDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(404);
    }


    @Test
    void testTeacherWritesGradePerStudentPerSubjectWhenNoAssignmentThisTeacherToTeachThisSubjectToTheStudentsClazzShouldReturn404NotFound() {
        //Arrange
        Student student9 = Student.<Student>find("identity", EGNSTUDENT1C_9).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();
        Teacher teacher3 = Teacher.<Teacher>find("identity", EGNTEACHER_3).firstResult();
        JsonObject gradeDtoJsonObject = Json.createObjectBuilder()
                .add("grade", 3)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .queryParam("studentId", student9.id)
                .queryParam("subjectId", subjectMath.id)
                .queryParam("teacherId", teacher3.id)
                .body(gradeDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(404);
    }




    @Test
    void testGetAllGradesPerStudentPerSubjectPageOne() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();

        Teacher expectedTeacherIs1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();

        given()
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("grade", hasItem(6))
                .body("teacherFullName", hasItem(expectedTeacherIs1.firstName + " " + expectedTeacherIs1.lastName));
    }

    @Test
    void testGetAllGradesPerStudentPerSubjectWhenPageTwoShouldReturnOneRecordOnly() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();

        Teacher expectedTeacherIs2 = Teacher.<Teacher>find("identity", EGNTEACHER_2).firstResult();

        given()
                .queryParam("page", 2)
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(1))
                .body("grade", hasItem(6))
                .body("teacherFullName", hasItem(expectedTeacherIs2.firstName + " " + expectedTeacherIs2.lastName));
    }

    @Test
    void testGetAllGradesPerStudentPerSubjectWhenPageFourShouldReturnEmptyList() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();


        given()
                .queryParam("page", 4)
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }

    @Test
    void testGetAllGradesPerStudentPerSubjectWhenPageOneAndStudentIdAndSubectIdAreNegativeShouldReturn400BadRequest() {
        //Arrange
        Long studentId = -300L;
        Long subjectId = -300L;


        given()
//                .queryParam("page", 1)
                .queryParam("studentId", studentId)
                .queryParam("subjectId", subjectId)
                .when().get()
                .then()
                .statusCode(400);
    }

    @Test
    void testGetAllGradesPerStudentPerSubjectWhenPageOneAndStudentIdAndSubectIdAreNotPresentInTheDBShouldReturn404NotFound() {
        //Arrange
        Long studentId = 25000L;
        Long subjectId = 25000L;


        given()
//                .queryParam("page", 1)
                .queryParam("studentId", studentId)
                .queryParam("subjectId", subjectId)
                .when().get()
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Student", is(String.format("No student with id %d present", studentId)))
                .body("Subject", is(String.format("No subject with id %d present", subjectId)));
    }

    @Test
    void testGetAllGradesPerStudentPerSubjectWhenPageOneAndStudentDoesNotHaveAClazzShouldReturn404NotFound() {
        //Arrange
        Student student7 = Student.<Student>find("identity", EGNSTUDENT1A_7).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();


        given()
//                .queryParam("page", 1)
                .queryParam("studentId", student7.id)
                .queryParam("subjectId", subjectMath.id)
                .when().get()
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Student", is(String.format("The student with id %d does not have a clazz yet", student7.id)));
    }

    @Test
    void testGetAllGradesPerStudentPerSubjectWhenPageOneAndNoAssignmentAnyTeacherToTeachThisSubjectToTheStudentsClazzShouldReturn404NotFound() {
        //Arrange
        Student student9_6C = Student.<Student>find("identity", EGNSTUDENT1C_9).firstResult();
        Subject subjectBiol = Subject.<Subject>find("name", SUBJECTBIOL_3.toLowerCase(Locale.ROOT)).firstResult();


        given()
//                .queryParam("page", 1)
                .queryParam("studentId", student9_6C.id)
                .queryParam("subjectId", subjectBiol.id)
                .when().get()
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Assignment",
                        is(String.format("There is still no such assignment for subject %s and clazz %s and/or no teacher assigned yet for sure",
                                subjectBiol, student9_6C.clazz)));
    }
}