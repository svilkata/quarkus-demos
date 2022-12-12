package com.vidasoft.diary.absence;

import com.vidasoft.diary.BasePrep;
import com.vidasoft.diary.model.Absence;
import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.model.Teacher;
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

import static com.vidasoft.diary.absence.AbsenceResource.ABSENCE_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
@TestHTTPEndpoint(AbsenceResource.class)
@Tag("restassured-integration")
class AbsenceResourceTest extends BasePrep {
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
    void testTeacherWritesAbsencePerStudentPerSubject() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();
        Teacher teacher1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        JsonObject absenceDtoJsonObject = Json.createObjectBuilder()
                .add("absenceHours", 1)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .queryParam("teacherId", teacher1.id)
                .body(absenceDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(201);


        //Get the last added student
        List<Absence> list = Absence.<Absence>findAll().list();
        Absence lastAddedAbsenceRedord = list.get(list.size() - 1);

        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + "/" + ABSENCE_RESOURCE_PATH + "/" + lastAddedAbsenceRedord.id);
    }


    @Test
    void testTeacherWritesAbsencePerStudentPerSubjectWhenNegativeIdsShouldReturn400BadRequest() {
        //Arrange
        Long studentId = -300L;
        Long subjectId = -300L;
        Long teacherId = -300L;
        JsonObject absenceDtoJsonObject = Json.createObjectBuilder()
                .add("absenceHours", 1)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", studentId)
                .queryParam("subjectId", subjectId)
                .queryParam("teacherId", teacherId)
                .body(absenceDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(400);
    }


    @Test
    void testTeacherWritesAbsencePerStudentPerSubjectWhenAbsenceDtoHasWrongAbsenceHoursFieldShouldReturn400BadRequest() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();
        Teacher teacher1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        JsonObject absenceDtoJsonObject = Json.createObjectBuilder()
                .add("absenceHours", 0)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .queryParam("teacherId", teacher1.id)
                .body(absenceDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(400);
    }


    @Test
    void testTeacherWritesAbsencePerStudentPerSubjectWhenIdsDoNotExistShouldReturn404NotFound() {
        //Arrange
        Long studentId = 25000L;
        Long subjectId = 25000L;
        Long teacherId = 25000L;
        JsonObject absenceDtoJsonObject = Json.createObjectBuilder()
                .add("absenceHours", 1)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", studentId)
                .queryParam("subjectId", subjectId)
                .queryParam("teacherId", teacherId)
                .body(absenceDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(404);
    }


    @Test
    void testTeacherWritesAbsencePerStudentPerSubjectWhenStudentDoesNotHaveClazzShouldReturn404NotFound() {
        //Arrange
        Student student7 = Student.<Student>find("identity", EGNSTUDENT1A_7).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();
        Teacher teacher1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        JsonObject absenceDtoJsonObject = Json.createObjectBuilder()
                .add("absenceHours", 3)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", student7.id)
                .queryParam("subjectId", subjectMath.id)
                .queryParam("teacherId", teacher1.id)
                .body(absenceDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(404);
    }


    @Test
    void testTeacherWritesAbsencePerStudentPerSubjectWhenNoAssignmentThisTeacherToTeachThisSubjectToTheStudentsClazzShouldReturn404NotFound() {
        //Arrange
        Student student9 = Student.<Student>find("identity", EGNSTUDENT1C_9).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();
        Teacher teacher3 = Teacher.<Teacher>find("identity", EGNTEACHER_3).firstResult();
        JsonObject absenceDtoJsonObject = Json.createObjectBuilder()
                .add("absenceHours", 3)
                .build();

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
//                .log().all()
                .queryParam("studentId", student9.id)
                .queryParam("subjectId", subjectMath.id)
                .queryParam("teacherId", teacher3.id)
                .body(absenceDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(404);
    }


    @Test
    void testGetAllAbsenceRecordsPerStudentPerSubjectPageOne() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();

        Teacher expectedTeacher1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();
        Teacher expectedTeacher2 = Teacher.<Teacher>find("identity", EGNTEACHER_2).firstResult();

        given()
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("absenceHours", hasItems(2, 1))
                .body("teacherFullName", hasItems(expectedTeacher1.firstName + " " + expectedTeacher1.lastName,
                        expectedTeacher2.firstName + " " + expectedTeacher2.lastName));
    }

    @Test
    void testGetAllAbsenceRecordsPerStudentPerSubjectWhenPageTwoShouldReturnOnlyOneAbsenceRecord() {
        //Arrange
        Student student1 = Student.<Student>find("identity", EGNSTUDENT1A_1).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();

        Teacher expectedTeacherIs1 = Teacher.<Teacher>find("identity", EGNTEACHER_1).firstResult();

        given()
                .queryParam("page", 2)
                .queryParam("studentId", student1.id)
                .queryParam("subjectId", subjectMath.id)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(1))
                .body("absenceHours", hasItem(1))
                .body("teacherFullName", hasItem(expectedTeacherIs1.firstName + " " + expectedTeacherIs1.lastName));
    }

    @Test
    void testGetAllAbsenceRecordsPerStudentPerSubjectWhenPageFourShouldReturnEmptyList() {
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
    void testGetAllAbsenceRecordsPerStudentPerSubjectWhenPageOneAndStudentIdAndSubectIdAreNegativeShouldReturn400BadRequest() {
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
    void testGetAllAbsenceRecordsPerStudentPerSubjectWhenPageOneAndStudentIdAndSubectIdAreNotPresentInTheDBShouldReturn404NotFound() {
        //Arrange
        Long studentId = 25000L;
        Long subjectId = 25000L;

        given()
                .queryParam("studentId", studentId)
                .queryParam("subjectId", subjectId)
                .when().get()
                .then()
                .statusCode(404);
    }


    @Test
    void testGetAllAbsenceRecordsPerStudentPerSubjectWhenPageOneAndStudentDoesNotHaveAClazzShouldReturn404NotFound() {
        //Arrange
        Student student7 = Student.<Student>find("identity", EGNSTUDENT1A_7).firstResult();
        Subject subjectMath = Subject.<Subject>find("name", SUBJECTMATH_1.toLowerCase(Locale.ROOT)).firstResult();

        given()
                .queryParam("studentId", student7.id)
                .queryParam("subjectId", subjectMath.id)
                .when().get()
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON);
    }


    @Test
    void testGetAllAbsenceRecordsPerStudentPerSubjectWhenPageOneAndNoAssignmentAnyTeacherToTeachThisSubjectToTheStudentsClazzShouldReturn404NotFound() {
        //Arrange
        Student student9_6C = Student.<Student>find("identity", EGNSTUDENT1C_9).firstResult();
        Subject subjectBiol = Subject.<Subject>find("name", SUBJECTBIOL_3.toLowerCase(Locale.ROOT)).firstResult();

        given()
                .queryParam("studentId", student9_6C.id)
                .queryParam("subjectId", subjectBiol.id)
                .when().get()
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON);
    }
}