package com.vida.absence;

import com.vida.BasePrepForTests;
import com.vida.model.Student;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import com.vida.student.StudentService;
import com.vida.subject.SubjectService;
import com.vida.teacher.TeacherService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(AbsenceRestResource.class)
@Tag("restassured-integration")
class AbsenceRestResourceTest extends BasePrepForTests {
    @Inject
    StudentService studentService;
    @Inject
    SubjectService subjectService;
    @Inject
    TeacherService teacherService;

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
    void testGetAllAbsencesPerStudentPerSubjectPageOneAndPageTwo() {
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        given()
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("absenceType", hasItems("OLYMPIAD", "HOSPITAL"))
                .body("countHours", hasItems(1, 2));


        given()
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .queryParam("page", 2)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(1))
                .body("absenceType", hasItem("STUDENTGUILTY"))
                .body("countHours", hasItem(2));
    }

    @Test
    void testGetAllAbsencesPerStudentPerSubjectWhenPageFourShouldReturnEmptyList() {
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        given()
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .queryParam("page", 4)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }


    @Test
    void testGetAllAbsencesPerStudentPerSubjectWhenPageOneAndStudentIdAndSubjectIdDoNotExistShouldReturn404NotFound() {
        given()
                .pathParam("studentId", 25000)
                .pathParam("subjectId", 25000)
//                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Student", anything("No Student with id"))
                .body("Subject", anything("No subject with id"));
    }


    @Test
    void testGetAllAbsencesPerStudentPerSubjectWhenPageOneAndStudentIdAndSubjectIdAreNegativeShouldReturn400BadRequest() {
        given()
                .pathParam("studentId", -300)
                .pathParam("subjectId", -300)
//                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(400);
    }


    @Test
    void testGetAllAbsencesPerStudentPerSubjectWhenPageOneAndStudentDoNotHaveAKlasShouldReturn404NotFound() {
        Student student_7 = studentService.getStudentByEgn(EGNSTUDENT1A_7).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        given()
                .pathParam("studentId", student_7.getId())
                .pathParam("subjectId", subjectMath_1.getId())
//                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Student", is(String.format("The student with id %d does not have a klas", student_7.getId())));
    }


    @Test
    void testGetAllAbsencesPerStudentPerSubjectWhenPageOneNoSuchAssignmentYetReturn404NotFound() {
        Student student_9_1C = studentService.getStudentByEgn(EGNSTUDENT1C_9).orElse(null);
        Subject subjectLitera_2 = subjectService.getSubjectBySubjectString(SUBJECTLITERA_2).orElse(null);

        given()
                .pathParam("studentId", student_9_1C.getId())
                .pathParam("subjectId", subjectLitera_2.getId())
//                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Assignment", anything(String.format("There is still no such assignment for subject %s and klas", subjectLitera_2)));
    }

    @Test
    void testTeacherWriteAbsencePerStudentPerSubject() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        assert subjectMath_1 != null;
        assert student_1 != null;
        assert teacher_1 != null;
        Integer absenceHours = 2;
        String absenceType = "STUDENTGUILTY";

//        {mark, teacherId}
        JsonObject absenceDto = Json.createObjectBuilder()
                .add("countHours", absenceHours)
                .add("absenceType", absenceType)
                .add("teacherId", teacher_1.getId())
                .build();

        //Act and Assert - saving the new mark
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .body(absenceDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(200)
                .body("size()", is(6))
                .body("countHours", is(absenceHours))
                .body("absenceType", is(absenceType));


        //Assert - checking if the new mark is in the database
        given()
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .queryParam("page", 2)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(2))
                .body("absenceType", hasItem("STUDENTGUILTY"))
                .body("countHours", hasItems(2));
    }


    @Test
    void testTeacherWriteAbsencePerStudentPerSubjectWhenTeacherIdAndSubjectIdAndStudentIdDoNoExistShouldReturn404NotFound() {
        //Arrange
        Integer absenceHours = 2;
        String absenceType = "STUDENTGUILTY";

//        {mark, teacherId}
        JsonObject absenceDto = Json.createObjectBuilder()
                .add("countHours", absenceHours)
                .add("absenceType", absenceType)
                .add("teacherId", 25000)
                .build();

        //Act and Assert - saving the new mark
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", 25000)
                .pathParam("subjectId", 25000)
                .body(absenceDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .body("Teacher", anything("No Teacher with id"))
                .body("Student", anything("No Student with id"))
                .body("Subject", anything("No Subject with id"));
    }


    @Test
    void testTeacherWriteAbsencePerStudentPerSubjectWhenTeacherIdAndOrSubjectIdAndOrStudentIdAreNegativeShouldReturn400BadRequest() {
        //Arrange
        Integer absenceHours = 2;
        String absenceType = "STUDENTGUILTY";

//        {mark, teacherId}
        JsonObject absenceDto = Json.createObjectBuilder()
                .add("countHours", absenceHours)
                .add("absenceType", absenceType)
                .add("teacherId", -300)
                .build();

        //Act and Assert - saving the new mark
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", -300)
                .pathParam("subjectId", -300)
                .body(absenceDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(400);
    }


    @Test
    void testTeacherWriteAbsencePerStudentPerSubjectWhenStudentDoesNotHaveAKlasShouldReturn404NotFound() {
        //Arrange
        Student student_7 = studentService.getStudentByEgn(EGNSTUDENT1A_7).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Integer absenceHours = 2;
        String absenceType = "STUDENTGUILTY";

//        {mark, teacherId}
        JsonObject absenceDto = Json.createObjectBuilder()
                .add("countHours", absenceHours)
                .add("absenceType", absenceType)
                .add("teacherId", teacher_1.getId())
                .build();

        //Act and Assert - saving the new mark
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", student_7.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .body(absenceDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .body("Student", is(String.format("The Student with id %d does not have a klas", student_7.getId())));
    }


    @Test
    void testTeacherWriteAbsencePerStudentPerSubjectWhenNoSuchAssignmentYetReturn404NotFound() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectHistory_5 = subjectService.getSubjectBySubjectString(SUBJECTHISTORY_5).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Integer absenceHours = 2;
        String absenceType = "STUDENTGUILTY";

//        {mark, teacherId}
        JsonObject absenceDto = Json.createObjectBuilder()
                .add("countHours", absenceHours)
                .add("absenceType", absenceType)
                .add("teacherId", teacher_1.getId())
                .build();

        //Act and Assert - saving the new mark
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectHistory_5.getId())
                .body(absenceDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .body("Assignment", anything("There is still no such assignment for subject"));
    }
}