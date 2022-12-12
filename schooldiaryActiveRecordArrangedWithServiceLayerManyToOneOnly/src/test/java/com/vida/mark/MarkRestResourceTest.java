package com.vida.mark;

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
@TestHTTPEndpoint(MarkRestResource.class)
@Tag("restassured-integration")
class MarkRestResourceTest extends BasePrepForTests {
    @Inject
    SubjectService subjectService;
    @Inject
    StudentService studentService;
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
    void testGetAllMarksPerStudentPerSubjectPageOneAndPageTwo() {
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
                .body("mark", hasItems(6));

        given()
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .queryParam("page", 2)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(1))
                .body("mark", hasItem(2));
    }

    @Test
    void testGetAllMarksPerStudentPerSubjectWhenPageFourShouldReturnEmptyList() {
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);

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
    void testGetAllMarksPerStudentPerSubjectWhenPageOneAndStudentIdAndSubjectIdDoNotExistShouldReturn404NotFound() {
        given()
                .pathParam("studentId", 25000)
                .pathParam("subjectId", 25000)
                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Student", anything("No Student with id"))
                .body("Subject", anything("No subject with id"));
    }

    @Test
    void testGetAllMarksPerStudentPerSubjectWhenPageOneAndStudentIdAndSubjectIdAreNegativeShouldReturn400BadRequest() {
        given()
                .pathParam("studentId", -300)
                .pathParam("subjectId", -300)
                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(400);
    }

    @Test
    void testGetAllMarksPerStudentPerSubjectWhenPageOneAndStudentDoNotHaveAKlasShouldReturn404NotFound() {
        Student student_7 = studentService.getStudentByEgn(EGNSTUDENT1A_7).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        given()
                .pathParam("studentId", student_7.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Student", is(String.format("The student with id %d does not have a klas", student_7.getId())));
    }

    @Test
    void testGetAllMarksPerStudentPerSubjectWhenPageOneNoSuchAssignmentYetReturn404NotFound() {
        Student student_9_1C = studentService.getStudentByEgn(EGNSTUDENT1C_9).orElse(null);
        Subject subjectLitera_2 = subjectService.getSubjectBySubjectString(SUBJECTLITERA_2).orElse(null);

        given()
                .pathParam("studentId", student_9_1C.getId())
                .pathParam("subjectId", subjectLitera_2.getId())
                .queryParam("page", 1)
                .when().get("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Assignment", anything(String.format("There is still no such assignment for subject %s and klas", subjectLitera_2)));
    }

    @Test
    void testTeacherWriteMarkPerStudentPerSubject() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        assert subjectMath_1 != null;
        assert student_1 != null;
        assert teacher_1 != null;
        Integer markTeacherToWriteToStudent = 5;

//        {mark, teacherId}
        JsonObject markDto = Json.createObjectBuilder()
                .add("mark", markTeacherToWriteToStudent)
                .add("teacherId", teacher_1.getId())
                .build();

        //Act and Assert - saving the new mark
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .body(markDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(200)
                .body("size()", is(5))
                .body("mark", is(markTeacherToWriteToStudent));


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
                .body("mark", hasItems(2, markTeacherToWriteToStudent));
    }

    @Test
    void testTeacherWriteMarkPerStudentPerSubjectWhenTeacherIdAndSubjectIdAndStudentIdDoNoExistShouldReturn404NotFound() {
        //Arrange
        Integer markTeacherToWriteToStudent = 5;

//        {mark, teacherId}
        JsonObject markDto = Json.createObjectBuilder()
                .add("mark", markTeacherToWriteToStudent)
                .add("teacherId", 25000)
                .build();

        //Act and Assert
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", 25000)
                .pathParam("subjectId", 25000)
                .body(markDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .body("Teacher", anything("No Teacher with id"))
                .body("Student", anything("No Student with id"))
                .body("Subject", anything("No Subject with id"));
    }

    @Test
    void testTeacherWriteMarkPerStudentPerSubjectWhenTeacherIdAndOrSubjectIdAndOrStudentIdAreNegativeShouldReturn400BadRequest() {
        //Arrange
        Integer markTeacherToWriteToStudent = 5;

//        {mark, teacherId}
        JsonObject markDto = Json.createObjectBuilder()
                .add("mark", markTeacherToWriteToStudent)
                .add("teacherId", -300)
                .build();

        //Act and Assert
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", -300)
                .pathParam("subjectId", -300)
                .body(markDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(400);
    }

    @Test
    void testTeacherWriteMarkPerStudentPerSubjectWhenStudentDoesNotHaveAKlasShouldReturn404NotFound() {
        //Arrange
        Student student_7 = studentService.getStudentByEgn(EGNSTUDENT1A_7).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Integer markTeacherToWriteToStudent = 5;

//        {mark, teacherId}
        JsonObject markDto = Json.createObjectBuilder()
                .add("mark", markTeacherToWriteToStudent)
                .add("teacherId", teacher_1.getId())
                .build();

        //Act and Assert
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", student_7.getId())
                .pathParam("subjectId", subjectMath_1.getId())
                .body(markDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .body("Student", is(String.format("The Student with id %d does not have a klas", student_7.getId())));
    }


    @Test
    void testTeacherWriteMarkPerStudentPerSubjectWhenNoSuchAssignmentYetReturn404NotFound() {
        //Arrange
        Student student_1 = studentService.getStudentByEgn(EGNSTUDENT1A_1).orElse(null);
        Subject subjectHistory_5 = subjectService.getSubjectBySubjectString(SUBJECTHISTORY_5).orElse(null);
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Integer markTeacherToWriteToStudent = 5;

//        {mark, teacherId}
        JsonObject markDto = Json.createObjectBuilder()
                .add("mark", markTeacherToWriteToStudent)
                .add("teacherId", teacher_1.getId())
                .build();

        //Act and Assert
        given()
                .contentType(APPLICATION_JSON)
                .pathParam("studentId", student_1.getId())
                .pathParam("subjectId", subjectHistory_5.getId())
                .body(markDto.toString())
                .when().post("/{studentId}/{subjectId}")
                .then()
                .statusCode(404)
                .body("Assignment", anything("There is still no such assignment for subject"));
    }
}