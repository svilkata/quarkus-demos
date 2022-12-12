package com.vida.klas;

import com.vida.assignment.AssignmentService;
import com.vida.klas.dto.KlasDto;
import com.vida.model.Klas;
import com.vida.model.Student;
import com.vida.student.StudentService;
import com.vida.student.dto.StudentDto;
import com.vida.teacher.TeacherMapStruct;
import com.vida.teacher.dto.TeacherDto;

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

import static com.vida.klas.KlasRestResource.KLAS_RESOURCE_PATH;

@Path(KLAS_RESOURCE_PATH)
public class KlasRestResource {
    public static final String KLAS_RESOURCE_PATH = "/klas";

    @Inject
    KlasService klasService;
    @Inject
    StudentService studentService;
    @Inject
    AssignmentService assignmentService;
    @Inject
    KlasMapStruct klasMapStruct;
    @Inject
    TeacherMapStruct teacherMapStruct;

    /*
    {
        "klasNumber": 3,
        "klasLetter": "A"
    }
    **/
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addNewKlas(@Valid @NotNull KlasDto klasDto) {
        Klas klas = klasMapStruct.fromKlasDtoToKlas(klasDto);
        Optional<Klas> createdKlas = klasService.addNewKlas(klas);

        return createdKlas
                .map(c -> klasMapStruct.fromKlasToKlasDto(createdKlas.get()))
                .map(cDto -> Response.ok(cDto).location(URI.create(KLAS_RESOURCE_PATH + "/" + cDto.getId())).build())
                .orElseGet(() -> Response.status(Response.Status.CONFLICT)
                        .entity(Json.createObjectBuilder()
                                .add("Comment", String.format("Klas %s already exists in the database", klasDto))
                                .build())
                        .build());
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{klasId}/student/{studentId}")
    @Transactional
    public Response assignKlasAStudent(@PathParam("klasId") @NotNull @Positive Long klId,
                                       @PathParam("studentId") @NotNull @Positive Long stId) {
        Klas klasAvailable = klasService.getKlasById(klId).orElse(null);
        Student studentAvailable = studentService.getStudentById(stId).orElse(null);

        Response response = checkKlasAndStudent(studentAvailable, klasAvailable, klId, stId);
        if (response != null) {
            return response;
        }

        Student studentAssignedToKlas = studentService.assignStudentToKlas(studentAvailable, klasAvailable);
        return Response.ok(new StudentDto(studentAssignedToKlas)).build();
    }


    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKlassById(@PathParam("id") @NotNull @Positive Long klasId) {
        return klasService.getKlasById(klasId)
                .map(c -> klasMapStruct.fromKlasToKlasDto(c))
                .map(cDTO -> Response.ok(cDTO).build())
                .orElseGet(() -> responseKlasNotFound(klasId));
    }

    //The ws.rs library takes the List<Klas> and makes a response 200 with the body List<Klas>
    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public List<KlasDto> getAllKlasses(@DefaultValue("1") @QueryParam("page") int page,
                                    @DefaultValue("4") @QueryParam("size") int size) {
        return klasService.getAllKlasses(page, size).stream().map(kl -> klasMapStruct.fromKlasToKlasDto(kl)).toList();
    }

    @GET()
    @Path("/{klasId}/students")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllKlasStudentsByKlasId(@PathParam("klasId") @NotNull @Positive Long klId,
                                               @DefaultValue("1") @QueryParam("page") int page,
                                               @DefaultValue("4") @QueryParam("size") int size) {
        Klas klas = klasService.getKlasById(klId).orElse(null);

        if (klas == null) {
            return responseKlasNotFound(klId);
        }

        List<StudentDto> studentsFromKlasDtosInPage = studentService.getAllStudentsByKlas(klas, page , size)
                .stream()
                .map(st -> new StudentDto(st))
                .toList();

        return Response.ok(studentsFromKlasDtosInPage).build();
    }


    @GET()
    @Path("/{klasId}/teachers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllKlasTeachersByKlasId(@PathParam("klasId") @NotNull @Positive Long klasId,
                                               @DefaultValue("1") @QueryParam("page") int page,
                                               @DefaultValue("4") @QueryParam("size") int size) {
        Klas klas = klasService.getKlasById(klasId).orElse(null);

        if (klas == null) {
            return responseKlasNotFound(klasId);
        }

        List<TeacherDto> teachersDtoInPage = assignmentService.getAllTeachersByKlas(klas, page, size)
                .stream()
                .map(s -> teacherMapStruct.fromTeacherToTeacherDto(s)).toList();

        return Response.ok(teachersDtoInPage).build();
    }

    private static Response responseKlasNotFound(Long klId) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("Comment", String.format("No klas with id %d present", klId)).build())
                .build();
    }

    private Response checkKlasAndStudent(Student studentAvailable, Klas klasAvailable, Long klId, Long stId) {
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
                                    String.format("For klas with id %d there is already assigned student with id %d", klId, stId))
                            .build())
                    .build();
        }

        return null;
    }

}
