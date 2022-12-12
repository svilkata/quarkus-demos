package com.vida.teacher;

import com.vida.assignment.AssignmentService;
import com.vida.klas.KlasMapStruct;
import com.vida.klas.KlasService;
import com.vida.klas.dto.KlasDto;
import com.vida.model.Klas;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import com.vida.subject.SubjectMapStruct;
import com.vida.subject.SubjectService;
import com.vida.subject.dto.SubjectDto;
import com.vida.teacher.dto.TeacherDto;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
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

import static com.vida.teacher.TeacherRestResource.TEACHER_RESOURCE_PATH;

@Path(TEACHER_RESOURCE_PATH)
public class TeacherRestResource {
    public static final String TEACHER_RESOURCE_PATH = "/teacher";

    @Inject
    TeacherService teacherService;
    @Inject
    KlasService klasService;
    @Inject
    SubjectService subjectService;
    @Inject
    AssignmentService assignmentService;
    @Inject
    SubjectMapStruct subjectMapStruct;
    @Inject
    KlasMapStruct klasMapStruct;
    @Inject
    TeacherMapStruct teacherMapStruct;


    /*
    {
        "firstName": "Gratsiela",
        "lastName": "Tabakova",
        "egn": "9292000092"
    }
    **/
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addNewTeacher(@Valid @NotNull TeacherDto teacherDto) {
        Teacher teacher = teacherMapStruct.fromTeacherDtoToTeacher(teacherDto);
        Optional<Teacher> createdTeacher = teacherService.addNewTeacher(teacher);

        return createdTeacher
                .map(c -> teacherMapStruct.fromTeacherToTeacherDto(createdTeacher.get()))
                .map(tDto -> Response.ok(tDto).location(URI.create(TEACHER_RESOURCE_PATH + "/" + tDto.getId())).build())
                .orElseGet(() -> Response.status(Response.Status.CONFLICT)
                        .entity(Json.createObjectBuilder()
                                .add("Comment", String.format("Teacher with EGN %s already exists in the database", teacherDto.getEgn()))
                                .build())
                        .build());
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTeachers(@DefaultValue("1") @QueryParam("page") int page,
                                   @DefaultValue("4") @QueryParam("size") int size) {
        List<TeacherDto> allTeachersDtosInPage = teacherService.getAllTeachers(page , size).stream().map(t -> teacherMapStruct.fromTeacherToTeacherDto(t)).toList();

        //if list is empty, we return an empty array
        return Response.ok(allTeachersDtosInPage).build();
    }

    @GET()
    @Path("/{teacherId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTeacherById(@PathParam("teacherId") @NotNull @Positive Long tId) {
        Teacher teacher = teacherService.getTeacherById(tId).orElse(null);

        if (teacher == null) {
            return responseNoObjectPresent("No teacher with id %d present", tId, null);
        } else {
            return Response.ok(teacherMapStruct.fromTeacherToTeacherDto(teacher)).build();
        }
    }

    @GET()
    @Path("/{teacherId}/subjects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubjectsByTeacherId(@PathParam("teacherId") @NotNull @Positive Long tId,
                                           @DefaultValue("1") @QueryParam("page") int page,
                                           @DefaultValue("4") @QueryParam("size") int size) {
        Teacher teacher = teacherService.getTeacherById(tId).orElse(null);

        if (teacher == null) {
            return responseNoObjectPresent("No teacher with id %d present", tId, null);
        } else {
            List<Subject> allSubjectsAssignedForATeacherInPage = assignmentService.getSubjectsForTeacher(teacher, page, size);
            List<SubjectDto> subjectsDtoInPage = allSubjectsAssignedForATeacherInPage.stream()
                    .map(subj -> subjectMapStruct.fromSubjectToSubjectDto(subj)).toList();

            return Response.ok(subjectsDtoInPage).build();
        }
    }

    @GET()
    @Path("/{teacherId}/klasses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKlassesByTeacherId(@PathParam("teacherId") @NotNull @Positive Long tId,
                                          @DefaultValue("1") @QueryParam("page") int page,
                                          @DefaultValue("4") @QueryParam("size") int size) {
        Teacher teacher = teacherService.getTeacherById(tId).orElse(null);

        if (teacher == null) {
            return responseNoObjectPresent("No teacher with id %d present", tId, null);
        } else {
            List<Klas> allKlassesAssignedForATeacherInPage = assignmentService.getKlassesForTeacher(teacher, page, size);
            List<KlasDto> klassesDtoInPage = allKlassesAssignedForATeacherInPage.stream()
                    .map(kls -> klasMapStruct.fromKlasToKlasDto(kls)).toList();

            return Response.ok(klassesDtoInPage).build();
        }
    }

    @GET()
    @Path("/{teacherId}/{klasId}/subjects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubjectsByTeacherIdAndKlasId(@PathParam("teacherId") @NotNull @Positive Long tId,
                                                    @PathParam("klasId") @NotNull @Positive Long klasId,
                                                    @DefaultValue("1") @QueryParam("page") int page,
                                                    @DefaultValue("4") @QueryParam("size") int size) {
        Teacher teacher = teacherService.getTeacherById(tId).orElse(null);
        Klas klas = klasService.getKlasById(klasId).orElse(null);

        if (teacher == null && klas == null) {
            return responseNoObjectPresent("No teacher with id %d and no klas with id %d present", tId, klasId);
        }

        if (teacher == null) {
            return responseNoObjectPresent("No teacher with id %d present", tId, null);
        }

        if (klas == null) {
            return responseNoObjectPresent("No klas with id %d present", klasId, null);
        }

        List<Subject> subjectsForTeacherAndKlasInPage = assignmentService.getSubjectsForTeacherAndKlas(teacher, klas, page, size);
        List<SubjectDto> subjectsDtoPerTeacherPerKlasInPage = subjectsForTeacherAndKlasInPage.stream()
                .map(subj -> subjectMapStruct.fromSubjectToSubjectDto(subj)).toList();

        return Response.ok(subjectsDtoPerTeacherPerKlasInPage).build();
    }

    @GET()
    @Path("/{teacherId}/{subjectId}/klasses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKlassesByTeacherIdAndSubjectId(@PathParam("teacherId") @NotNull @Positive Long tId,
                                                      @PathParam("subjectId") @NotNull @Positive Long subjId,
                                                      @DefaultValue("1") @QueryParam("page") int page,
                                                      @DefaultValue("4") @QueryParam("size") int size) {
        Teacher teacher = teacherService.getTeacherById(tId).orElse(null);
        Subject subject = subjectService.getSubjectById(subjId).orElse(null);

        if (teacher == null && subject == null) {
            return responseNoObjectPresent("No teacher with id %d and no subject with id %d present", tId, subjId);
        }

        if (teacher == null) {
            return responseNoObjectPresent("No teacher with id %d present", tId, null);
        }

        if (subject == null) {
            return responseNoObjectPresent("No subject with id %d present", subjId, null);
        }

        List<Klas> assignmentsForTeacherAndSubject = assignmentService.getKlassesForTeacherAndSubject(teacher, subject, page, size);
        List<KlasDto> klassesPerTeacherPerSubject = assignmentsForTeacherAndSubject.stream()
                .map(kls -> klasMapStruct.fromKlasToKlasDto(kls)).toList();

        return Response.ok(klassesPerTeacherPerSubject).build();
    }


    private static Response responseNoObjectPresent(String message, Long id1, Long id2) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("Comment", String.format(message, id1, id2))
                        .build())
                .build();
    }
}
