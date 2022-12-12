package com.vida.teacher;

import com.vida.BasePrepForTests;
import com.vida.klas.KlasService;
import com.vida.model.Klas;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import com.vida.subject.SubjectRestResource;
import com.vida.subject.SubjectService;
import com.vida.subject.dto.SubjectDto;
import com.vida.teacher.dto.TeacherDto;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import java.util.List;
import java.util.Optional;

import static com.vida.subject.SubjectRestResource.SUBJECT_RESOURCE_PATH;
import static com.vida.teacher.TeacherRestResource.TEACHER_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
//@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(TeacherRestResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("restassured-integration")
class TeacherRestResourceTest extends BasePrepForTests {
    @Inject
    TeacherService teacherService;
    @Inject
    TeacherMapStruct teacherMapStruct;
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
    @Order(1)
    void testGetTeacherById() {
        Teacher teacherByEgn = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", teacherByEgn.getId())
                .when().get("/{teacherId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("egn", is(EGNTEACHER_1));
    }

    @Test
    @Order(2)
    void testGetTeacherByIdWhenTeacherIdDoesNotExistShouldReturn404NotFound() {
        given()
                .pathParam("teacherId", 2500)
                .when().get("/{teacherId}")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("No teacher with id"));
    }

    @Test
    @Order(3)
    void testGetTeacherByIdWhenTeacherIdIsNegativeShouldReturn400BadRequest() {
        given()
                .pathParam("teacherId", -300)
                .when().get("/{teacherId}")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(4)
    void testAddNewTeacher() {
        // Creates a teacherDto instance
        TeacherDto teacherDto = new TeacherDto("Rovel" ,"Rovelov", "rovelrovel");

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonTeacherDto = jsonb.toJson(teacherDto);

        ValidatableResponse vr = given()
                .contentType(APPLICATION_JSON)
                .body(jsonTeacherDto)
                .when().post()
                .then()
                .statusCode(200)
                .body("firstName", is(teacherDto.getFirstName()));

        List<Teacher> list = Teacher.<Teacher>findAll().list();

        //Test the last added teacher url
        vr
                .assertThat()
                .header("Location", TEST_BASE_URLPATH + TEACHER_RESOURCE_PATH + "/" + list.get(list.size()-1).getId());
    }

    @Test
    @Order(5)
    void testAddNewTeacherWhenTeacherAlreadyExistsShouldReturn409Conflict() {
        // Creates a teacherDto instance
        TeacherDto teacherDto = teacherMapStruct.fromTeacherToTeacherDto(teacherService.getTeacherByEgn(EGNTEACHER_1).get());
        teacherDto.setId(null);

        // Creates Jsonb using a builder
        Jsonb jsonb = JsonbBuilder.create();

        // Serialises
        String jsonTeacherDto = jsonb.toJson(teacherDto);

        given()
                .contentType(APPLICATION_JSON)
                .body(jsonTeacherDto)
                .when().post()
                .then()
                .statusCode(409)
                .body("Comment", anything("already exists in the database"));
    }

    //!!!We can test here also some of the validated fields of the TeacherDto
    @Test
    @Order(6)
    void testAddNewTeacherWhenTeacherDtoNullShouldReturn400BadRequest() {
        given()
                .contentType(APPLICATION_JSON)
                .body(JsonbBuilder.create().toJson(null))
                .when().post()
                .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    void testGetAllTeachersPageOne() {
        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("egn", hasItems(EGNTEACHER_1, EGNTEACHER_2, EGNTEACHER_3, EGNTEACHER_4));
    }

    @Test
    @Order(8)
    void testGetAllTeachersPageThreeShouldReturnEmptyList() {
        given()
                .queryParam("page", 3)
                .queryParam("size", 4)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }


    @Test
    @Order(9)
    void testGetSubjectsByTeacherIdPageOne() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .queryParam("page", 1)
                .queryParam("size", 4)
                .when().get("/{teacherId}/subjects")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(3))
                .body("subject", hasItems(SUBJECTMATH_1, SUBJECTLITERA_2, SUBJECTSPORT_6));
    }

    @Test
    @Order(10)
    void testGetSubjectsByTeacherIdPageThreeShouldReturnEmptyList() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .queryParam("page", 3)
                .queryParam("size", 4)
                .when().get("/{teacherId}/subjects")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
      }

    @Test
    @Order(11)
    void testGetSubjectsByTeacherIdWhenTeacherIdDoesNotExistShouldReturn404NotFound() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", 2500)
                .queryParam("page", 1)
                .queryParam("size", 4)
                .when().get("/{teacherId}/subjects")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("No teacher with id"));
    }

    @Test
    @Order(12)
    void testGetSubjectsByTeacherIdWhenTeacherIdIsNegativeShouldReturn400BadRequst() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", -300)
                .when().get("/{teacherId}/subjects")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(13)
    void testGetKlassesByTeacherIdPageOne() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", teacher_1.getId())
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/klasses")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(3))
                .body("klasNumber", hasItems(1))
                .body("klasLetter", hasItems("A", "B", "C"));
    }

    @Test
    @Order(14)
    void testGetKlassesByTeacherIdWhenPageThreeShouldReturnEmptyList() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .queryParam("page", 3)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/klasses")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }

    @Test
    @Order(15)
    void testGetKlassesByTeacherIdWhenTeacherIdDoesNotExistShouldReturn404NotFound() {
        given()
                .pathParam("teacherId", 2500)
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/klasses")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Content", anything("No teacher with id"));
    }

    @Test
    @Order(16)
    void testGetKlassesByTeacherIdWhenTeacherIdIsNegativeShouldReturn400BadRequst() {
        given()
                .pathParam("teacherId", -300)
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/klasses")
                .then()
                .statusCode(400);
    }

    @Test
    @Order(17)
    void testGetSubjectsByTeacherIdAndKlasIdPageOne() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .pathParam("klasId", klas1A.getId())
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/{klasId}/subjects")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(2))
                .body("subject", hasItems(SUBJECTMATH_1, SUBJECTLITERA_2));
    }

    @Test
    @Order(18)
    void testGetSubjectsByTeacherIdAndKlasIdWhenPageThreeShouldReturnEmptyList() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .pathParam("klasId", klas1A.getId())
                .queryParam("page", 3)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/{klasId}/subjects")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }

    @Test
    @Order(19)
    void testGetSubjectsByTeacherIdAndKlasIdWhenTeacherIdDoesNotExistShouldReturn404NotFound() {
        Klas klas1A = klasService.getAllKlasses(1, 4).get(0);

        given()
                .pathParam("teacherId", 2500)
                .pathParam("klasId", klas1A.getId())
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/{klasId}/subjects")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("No teacher with id"));
    }

    @Test
    @Order(20)
    void testGetSubjectsByTeacherIdAndKlasIdWhenKlasIdDoesNotExistShouldReturn404NotFound() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .pathParam("klasId", 2500)
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/{klasId}/subjects")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("No klas with id"));
    }

    @Test
    @Order(20)
    void testGetSubjectsByTeacherIdAndKlasIdWhenBothTeacherIdAndKlasIdDoNotExistShouldReturn404NotFound() {
        given()
                .pathParam("teacherId", 2500)
                .pathParam("klasId", 2500)
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/{klasId}/subjects")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("no klas with id"))
                .body("Comment", anything("No teacher with id"));
    }

    @Test
    @Order(21)
    void testGetSubjectsByTeacherIdAndKlasIdWhenTeacherIdAndOrKlasIdAreNegstiveShouldReturn400BadRequest() {
        given()
                .pathParam("teacherId", -300)
                .pathParam("klasId", -300)
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/{klasId}/subjects")
                .then()
                .statusCode(400);
    }


    @Test
    @Order(22)
    void testGetKlassesByTeacherIdAndSubjectIdPageOne() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Subject subject_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .pathParam("subjectId", subject_1.getId())
//                .queryParam("page", 1)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/{subjectId}/klasses")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(2))
                .body("klasNumber", hasItems(1))
                .body("klasLetter", hasItems("A", "B"));
    }

    @Test
    @Order(23)
    void testGetKlassesByTeacherIdAndSubjectIdWhenPageThreeShouldReturnEmptyList() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);
        Subject subject_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .pathParam("subjectId", subject_1.getId())
                .queryParam("page", 3)
//                .queryParam("size", 4)
                .when().get("/{teacherId}/{subjectId}/klasses")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }

    @Test
    @Order(24)
    void testGetKlassesByTeacherIdAndSubjectIdWhenTeacherIdDoesNotExistShouldReturn404NotFound() {
        Subject subject_1 = subjectService.getSubjectBySubjectString(SUBJECTMATH_1).orElse(null);

        given()
                .pathParam("teacherId", 2500)
                .pathParam("subjectId", subject_1.getId())
                .when().get("/{teacherId}/{subjectId}/klasses")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("No teacher with id"));
    }

    @Test
    @Order(25)
    void testGetKlassesByTeacherIdAndSubjectIdWhenSubjectIdDoesNotExistShouldReturn404NotFound() {
        Teacher teacher_1 = teacherService.getTeacherByEgn(EGNTEACHER_1).orElse(null);

        given()
                .pathParam("teacherId", teacher_1.getId())
                .pathParam("subjectId", 2500)
                .when().get("/{teacherId}/{subjectId}/klasses")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("No subject with id"));
    }

    @Test
    @Order(26)
    void testGetKlassesByTeacherIdAndSubjectIdWhenBothTeacherIdAndSubjectIdDoNotExistShouldReturn404NotFound() {
        given()
                .pathParam("teacherId", 2500)
                .pathParam("subjectId", 2500)
                .when().get("/{teacherId}/{subjectId}/klasses")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("Comment", anything("no subject with id"))
                .body("Comment", anything("No teacher with id"));
    }

    @Test
    @Order(27)
    void testGetKlassesByTeacherIdAndSubjectIdWhenTeacherIdAndOrSubjectIdAreNegativeShouldReturn400BadRequest() {
        given()
                .pathParam("teacherId", -300)
                .pathParam("subjectId", -300)
                .when().get("/{teacherId}/{subjectId}/klasses")
                .then()
                .statusCode(400);
    }
}