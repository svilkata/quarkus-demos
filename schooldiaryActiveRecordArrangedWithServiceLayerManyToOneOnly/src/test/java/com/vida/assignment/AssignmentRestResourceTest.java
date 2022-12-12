package com.vida.assignment;

import com.vida.BasePrepForTests;
import com.vida.klas.KlasService;
import com.vida.model.Assignment;
import com.vida.model.Klas;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import com.vida.student.StudentService;
import com.vida.subject.SubjectService;
import com.vida.teacher.TeacherRestResource;
import com.vida.teacher.TeacherService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;

import java.util.List;

import static com.vida.assignment.AssignmentRestResource.ASSIGN_RESOURCE_PATH;
import static com.vida.teacher.TeacherRestResource.TEACHER_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(AssignmentRestResource.class)
@Tag("restassured-integration")
class AssignmentRestResourceTest extends BasePrepForTests {
    @Inject
    TeacherService teacherService;
    @Inject
    KlasService klasService;
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
    void testAssignSubjectAndKlas() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Subject subject_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Klas klas1D = klasService.getAllKlasses(1, 4).get(3);

        JsonObject assignmentDto = Json.createObjectBuilder()
                .add("teacherId", teacher_1.getId())
                .add("subjectId", subject_1.getId())
                .add("klasId", klas1D.getId())
                .build();


        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .body(assignmentDto.toString())
                .when().post()
                .then()
                .statusCode(200)
                .body("size()", is(4))
                .body("teacherId", is(Integer.parseInt(teacher_1.getId() + "")))
                .body("subjectId", is(Integer.parseInt(subject_1.getId() + "")))
                .body("klasId", is(Integer.parseInt(klas1D.getId() + "")));

        List<Assignment> list = Assignment.<Assignment>findAll().list();

        //Test the last added assignment url
        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + ASSIGN_RESOURCE_PATH + "/" + list.get(list.size()-1).getId());
    }

    @Test
    void testAssignSubjectAndKlasWhenTeacherIdAndSubjectIdAndKlasIdDoNotExistShouldReturn404NotFound() {
        JsonObject assignmentDto = Json.createObjectBuilder()
                .add("teacherId", 25000)
                .add("subjectId", 25000)
                .add("klasId", 25000)
                .build();


        given()
                .contentType(APPLICATION_JSON)
                .body(assignmentDto.toString())
                .when().post()
                .then()
                .statusCode(404)
                .body("Teacher", anything("No teacher with id"))
                .body("Subject", anything("No subject with id"))
                .body("Klas", anything("No klas with id"));
    }

    @Test
    void testAssignSubjectAndKlasWhenTeacherIdAndSubjectIdAndKlasIdAreNegtiveShouldReturn400BadRequest() {
        JsonObject assignmentDto = Json.createObjectBuilder()
                .add("teacherId", -300)
                .add("subjectId", -300)
                .add("klasId", -300)
                .build();


        given()
                .contentType(APPLICATION_JSON)
                .body(assignmentDto.toString())
                .when().post()
                .then()
                .statusCode(400);
    }

    @Test
    void testAssignSubjectAndKlasWhenAssignmentAlreadyExistShouldReturn409Conflict() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Subject subjectMath_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        JsonObject assignmentDto = Json.createObjectBuilder()
                .add("teacherId", teacher_1.getId())
                .add("subjectId", subjectMath_1.getId())
                .add("klasId", klas1A.getId())
                .build();


        given()
                .contentType(APPLICATION_JSON)
                .body(assignmentDto.toString())
                .when().post()
                .then()
                .statusCode(409)
                .body("Comment", anything("already exists in the database"));
    }
}