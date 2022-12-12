package com.vida.student;

import com.vida.klas.KlasService;
import com.vida.model.Klas;
import com.vida.model.Student;
import com.vida.student.dto.StudentDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.vida.student.StudentRestResource.STUDENT_RESOURCE_PATH;

@Path(STUDENT_RESOURCE_PATH)
public class StudentRestResource {
    public static final String STUDENT_RESOURCE_PATH = "/student";

    @Inject
    StudentService studentService;
    @Inject
    KlasService klasService;

    /*
    {
        "firstName": "Petkan",
        "lastName": "Petkanov",
        "egn": "eeeeeeeeee"
    }
    **/
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addNewStudent(@Valid @NotNull StudentDto studentDto) {
        Student studentAvailable = studentService.getStudentByEgn(studentDto.getEgn()).orElse(null);

        if (studentAvailable != null) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(Json.createObjectBuilder().add("Conflict",
                            String.format("Student with EGN %s already exists in the database", studentDto.getEgn())).build())
                    .build();
        }

        Student studentToAdd = studentService.addNewStudent(studentDto.toStudent()).orElse(null);
        StudentDto studentDtoFinal = new StudentDto(studentToAdd);

        return Response.ok(studentDtoFinal).location(URI.create(STUDENT_RESOURCE_PATH + "/" + studentDtoFinal.getId())).build();
    }


    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{studentId}/klas/{klasId}")
    @Transactional
    public Response assignStudentToKlas(@PathParam("studentId") @NotNull @Positive Long stId,
                                        @PathParam("klasId") @NotNull @Positive Long klId) {
        Student studentAvailable = studentService.getStudentById(stId).orElse(null);
        Klas klasAvailable = klasService.getKlasById(klId).orElse(null);

        Response response = checkStudentAndKlas(studentAvailable, klasAvailable, stId, klId);
        if (response != null) {
            return response;
        }

        Student studentWithAddedKlas = studentService.assignStudentToKlas(studentAvailable, klasAvailable);
        return Response.ok(new StudentDto(studentWithAddedKlas)).build();
    }



    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStudents(@DefaultValue("1") @QueryParam("page") int page,
                                   @DefaultValue("4") @QueryParam("size") int size) {
        List<Student> allStudentsInPage = studentService.getAllStudents(page, size);

        //if list is empty, we return an empty array
        return Response.ok(allStudentsInPage.stream().map(st -> new StudentDto(st)).toList())
                .build();
    }


    //        @Pattern(regexp = "^[0-9]+$")
    @GET()
    @Path("/{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudentById(@PathParam("studentId") @NotNull @Positive Long stId) {
        Optional<Student> studentByIdOpt = studentService.getStudentById(stId);

        return studentByIdOpt
                .map(st -> new StudentDto(st))
                .map(stDto -> Response.ok(stDto).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity(Json.createObjectBuilder()
                                .add("Comment", String.format("The student with id %d is not present", stId))
                                .build())
                        .build());
    }


    private Response checkStudentAndKlas(Student studentAvailable, Klas klasAvailable, Long stId, Long klId) {
        if (studentAvailable == null || klasAvailable == null) {
            JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
            if (studentAvailable == null) {
                jsonBuilder.add("NotFound", String.format("Student with id %d is not present", stId));
            }
            if (klasAvailable == null) {
                jsonBuilder.add("NotFound", String.format("Klas with id %d is not present", klId));
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build()).build();
        }

        if (studentAvailable.getKlas() != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Json.createObjectBuilder()
                            .add("Conflict",
                                    String.format("For student with id %d there is already assigned klas with id %d", stId, klId))
                            .build())
                    .build();
        }

        return null;
    }
}