package com.vida.student;

import com.vida.klas.KlasService;
import com.vida.model.Klas;
import com.vida.model.Student;
import com.vida.model.enums.KlasLetterEnum;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.transaction.Transactional;

import static com.vida.BasePrepForTests.TEST_BASE_URLPATH;
import static com.vida.student.StudentRestResource.STUDENT_RESOURCE_PATH;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.*;

/*
@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
@TestHTTPEndpoint(StudentRestResource.class)  //we start testing from url /student
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("restassured-integration")
@Transactional
class StudentRestResourceTest {
    @Inject
    StudentService studentService;
    @Inject
    KlasService klasService;
    private final String EGNSTUDENT1A_1 = "testtestte";
    private final String EGNSTUDENT1A_2 = "6666666661";
    private final String EGNSTUDENT1A_3 = "vvvvvvvvvv";
    private final String EGNSTUDENT1A_4 = "bbbbbbbbbb";


    @Test
    @Order(1)
    void testGetAllStudentsWhenNoStudentsCreatedYet() {
        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));

        //Preparation for Order(2) test method
        Klas klas1A = new Klas(1, KlasLetterEnum.A);
        klasService.addNewKlasInitial(klas1A);
        Student student1A_1 = new Student("Svilen", "Velikov", EGNSTUDENT1A_1).setKlas(klas1A);
        Student student1A_2 = new Student("Georgi", "Georgiev", EGNSTUDENT1A_2).setKlas(klas1A);
        Student student1A_3 = new Student("Vassil", "Levski", EGNSTUDENT1A_3).setKlas(klas1A);
        Student student1A_4 = new Student("Boiko", "Borisov", EGNSTUDENT1A_4).setKlas(klas1A);
        Student student1A_5 = new Student("Zahari", "Stoyanov", "zzzzzzzzzz").setKlas(klas1A);
        Student student1A_6 = new Student("Kiril", "Petkov", "kkkkkkkkkk").setKlas(klas1A);
        Student student1A_7 = new Student("Gospodin", "Gospodinov", "gggggggggg").setKlas(null);
        studentService.addNewStudent(student1A_1);
        studentService.addNewStudent(student1A_2);
        studentService.addNewStudent(student1A_3);
        studentService.addNewStudent(student1A_4);
        studentService.addNewStudent(student1A_5);
        studentService.addNewStudent(student1A_6);
        studentService.addNewStudent(student1A_7);
    }

    @Test
    @Order(2)
    void testGetAllStudentsWhenPageOne() {
        given()
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(4))
                .body("egn", hasItems(EGNSTUDENT1A_1, EGNSTUDENT1A_2, EGNSTUDENT1A_3, EGNSTUDENT1A_4));
    }

    @Test
    @Order(3)
    void testGetAllStudentsWhenPage3ShouldReturnEmptyList() {
        given()
                .queryParam("page", 3)
                .when().get()
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(0));
    }


    //да не казвам successfull - Подразбира се, че теста е successfull
    @Test
    @Order(4)
    void testGetStudentById() {
        given()
                .pathParam("studentId", 1)
                .when().get("/{studentId}")
                .then()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("size()", is(6))
                .body("id", equalTo(1))
                .body("firstName", is("Svilen"))
                .body("lastName", is("Velikov"))
                .body("egn", equalTo("testtestte"));
    }

    @Test
    @Order(5)
    void testGetStudentByIdWhenStudentDoesNotExist() {
        given()
                .when().get("/15")
                .then()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("size()", is(1))
                .body("Comment", anything("The student with id")); //similar to like comparison
    }

    @Test
    @Order(6)
    void testGetStudentByIdWhenStudentIdIsNegative() {
        given()
                .when().get("/-5")
                .then()
                .statusCode(400)
                .contentType(APPLICATION_JSON);
    }

    //it should return 400 Bad request the below test - it thinks it returns new url path
//    @Test
//    @Order(?)
//    void getStudentByIdWhenStudentIdIsIncorrectValue() {
//        given()
//                .when().get("/dcqce")
//                .then()
//                .statusCode(404)
//                .contentType(APPLICATION_JSON);
//    }



    @Test
    @Order(7)
    void testAddNewStudent() {
        JsonObject studentLikeDtoJsonObject = Json.createObjectBuilder()
                .add("firstName", "Pavel")
                .add("lastName", "Pavlovich")
                .add("egn", "pavelpavel")
                .build();

        given()
                .contentType(APPLICATION_JSON)
                .body(studentLikeDtoJsonObject.toString())
                .when().post()
                .then()
                .statusCode(200)
                .body("egn", is("pavelpavel"))
                .header("Location", TEST_BASE_URLPATH +  STUDENT_RESOURCE_PATH + "/8");
    }

    @Test
    @Order(8)
    void testAddNewStudentWhenStudentAlreadyExistsShouldReturn409Conflict() {
        JsonObject studentDto = Json.createObjectBuilder()
                .add("firstName", "Svilen")
                .add("lastName", "Velikov")
                .add("egn", EGNSTUDENT1A_1)
                .build();


        given()
                .contentType(APPLICATION_JSON)
                .body(studentDto.toString())
                .when().post()
                .then()
                .statusCode(409)
                .body("Conflict", anything("Student with EGN"));
    }

    //!!!We can test here also some of the validated fields of the StudentDto
    @Test
    @Order(9)
    void testAddNewStudentWhenStudentDtoNullShouldReturn400BadRequest() {
        JsonObject studentDto = Json.createObjectBuilder()
                   .build();

        given()
                .contentType(APPLICATION_JSON)
                .body(studentDto.toString())
                .when().post()
                .then()
                .statusCode(400);
    }


    @Test
    @Order(10)
    void testAssignStudentToKlas() {
        given()
                .pathParam("studentId", 7)
                .pathParam("klasId", 1)
                .when().patch("/{studentId}/klas/{klasId}")
//                .when().patch("/{studentId}/klas/{klasId}", 7, 1)

                .then()
                .log().all()
//                .assertThat()
                .statusCode(200)
                .contentType(APPLICATION_JSON)
                .body("egn", is("gggggggggg"))
                .body("klasName", is("1A"));
    }

    @Test
    @Order(11)
    void testAssignStudentToKlasWhenBothStudentIdAndKlasIdAreNotPresent() {
        given()
                .pathParam("studentId", 15)
                .pathParam("klasId", 25)
                .when().patch("/{studentId}/klas/{klasId}")

                .then()
                .log().all()
//                .assertThat()
                .statusCode(404)
                .contentType(APPLICATION_JSON)
                .body("NotFound", anything("is not present"));
    }

    @Test
    @Order(12)
    void testAssignStudentToKlasWhenStudentIdOrKlasIdNegativeNumbers() {
        given()
                .pathParam("studentId", -15)
                .pathParam("klasId", 25)
                .when().patch("/{studentId}/klas/{klasId}")

                .then()
                .log().all()
//                .assertThat()
                .statusCode(400)
                .contentType(APPLICATION_JSON);
    }

    @Test
    @Order(13)
    void testAssignStudentToKlasWhenStudentAlreadyAssignedToKlasShouldReturnConflict() {
        given()
                .pathParam("studentId", 1)
                .pathParam("klasId", 1)
                .when().patch("/{studentId}/klas/{klasId}")

                .then()
                .log().all()
//                .assertThat()
                .statusCode(409)
                .contentType(APPLICATION_JSON)
                .body("Conflict", anything("there is already assigned klas"));
    }
}

 */