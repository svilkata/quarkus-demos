package com.vidasoft.diary.teacher;

import com.vidasoft.diary.clazz.ClazzDTO;
import com.vidasoft.diary.model.Clazz;
import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.model.Teacher;
import com.vidasoft.diary.subject.SubjectDTO;
import com.vidasoft.diary.validation.Identity;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.vidasoft.diary.model.Clazz.GET_CLAZZES_BY_TEACHER;
import static com.vidasoft.diary.model.Clazz.GET_CLAZZES_BY_TEACHER_AND_SUBJECT;
import static com.vidasoft.diary.model.Subject.GET_SUBJECTS_BY_TEACHER;
import static com.vidasoft.diary.model.Subject.GET_SUBJECTS_BY_TEACHER_AND_CLAZZ;
import static com.vidasoft.diary.teacher.TeacherResource.TEACHER_RESOURCE_PATH;

@RequestScoped
@Path(TEACHER_RESOURCE_PATH)
public class TeacherResource {

    public static final String TEACHER_RESOURCE_PATH = "teacher";

    /*
    {
        "firstName": "Marieta",
        "lastName": "Tabakova",
        "identity": "1111111111"
    }
     */
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createTeacher(@Valid @NotNull TeacherDTO teacherDTO) {
        Teacher teacher = teacherDTO.toTeacher();
        teacher.persist();

        return Response.created(URI.create(String.format("/%s/%d", TEACHER_RESOURCE_PATH, teacher.id))).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<TeacherDTO> getAllTeachers(@QueryParam("page") @DefaultValue("1") int page,
                                           @QueryParam("size") @DefaultValue("4") int size) {
        return Teacher.<Teacher>findAll()
                .page(page - 1, size)
                .stream()
                .map(TeacherDTO::new)
                .collect(Collectors.toList());
    }

    @GET
    @Path("identity/{identity}")
    @Produces(MediaType.APPLICATION_JSON)
    public TeacherDTO getTeacherByIdentity(@PathParam("identity") @Identity String identity) {
        return Teacher.<Teacher>find("identity", identity)
                .firstResultOptional()
                .map(TeacherDTO::new)
                .orElseThrow(() -> new NotFoundException()); //връща 404 Not Found
    }

    @GET
    @Path("{teacherId}/clazzes")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClazzesOfATeacher(@PathParam("teacherId") @NotNull @Positive Long teacherId,
                                         @DefaultValue("1") @QueryParam("page") int page,
                                         @DefaultValue("4") @QueryParam("size") int size) {
        //No need to check if teacher exists in the database, we just return the list
        List<Clazz> resultList = Clazz.<Clazz>find("#" + GET_CLAZZES_BY_TEACHER, teacherId).page(page - 1, size).list();

        return Response.ok(resultList.stream().map(ClazzDTO::new).collect(Collectors.toList())).build();
    }

    @GET
    @Path("{teacherId}/subjects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubjectsOfATeacher(@PathParam("teacherId") @NotNull @Positive Long teacherId,
                                          @DefaultValue("1") @QueryParam("page") int page,
                                          @DefaultValue("4") @QueryParam("size") int size) {
        //No need to check if teacher exists in the database
        List<Subject> resultList = Subject.<Subject>find("#" + GET_SUBJECTS_BY_TEACHER, teacherId).page(page - 1, size).list();

        return Response.ok(resultList.stream().map(SubjectDTO::new).collect(Collectors.toList())).build();
    }


    @GET
    @Path("{teacherId}/clazzes/{subjectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClazzesByTeacherAndSubject(@PathParam("teacherId") @NotNull @Positive Long teacherId,
                                                  @PathParam("subjectId") @NotNull @Positive Long subjectId,
                                                  @DefaultValue("1") @QueryParam("page") int page,
                                                  @DefaultValue("4") @QueryParam("size") int size) {
        //No need to check if teacher and subject exist in the database

        List<Clazz> resultList = Clazz.<Clazz>find("#" + GET_CLAZZES_BY_TEACHER_AND_SUBJECT, teacherId, subjectId)
                .page(page - 1, size).list();

        return Response.ok(resultList.stream().map(ClazzDTO::new).collect(Collectors.toList())).build();
    }

    @GET
    @Path("{teacherId}/subjects/{clazzId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubjectsByTeacherAndClazz(@PathParam("teacherId") @NotNull @Positive Long teacherId,
                                                 @PathParam("clazzId") @NotNull @Positive Long clazzId,
                                                 @DefaultValue("1") @QueryParam("page") int page,
                                                 @DefaultValue("4") @QueryParam("size") int size) {
        //No need to check if teacher and clazz exist in the database
        List<Subject> resultList = Subject.<Subject>find("#" + GET_SUBJECTS_BY_TEACHER_AND_CLAZZ, teacherId, clazzId)
                .page(page - 1, size).list();

        return Response.ok(resultList.stream().map(SubjectDTO::new).collect(Collectors.toList())).build();
    }
}
