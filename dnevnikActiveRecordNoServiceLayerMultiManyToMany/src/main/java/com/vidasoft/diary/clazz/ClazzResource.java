package com.vidasoft.diary.clazz;

import com.vidasoft.diary.model.Clazz;
import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.model.Teacher;
import com.vidasoft.diary.validation.ClazzName;

import javax.enterprise.context.RequestScoped;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.vidasoft.diary.clazz.ClazzResource.CLAZZ_RESOURCE_PATH;
import static com.vidasoft.diary.model.Clazz.GET_CLAZZES_BY_TEACHER;

@RequestScoped
@Path(CLAZZ_RESOURCE_PATH)
public class ClazzResource {
    public static final String CLAZZ_RESOURCE_PATH = "clazz";

    /*
    {
        "clazzNumber": "3",
        "subclazzInitial": "A"
    }
     */
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createClazz(@Valid @NotNull ClazzDTO clazzDTO) {
        Clazz clazz = clazzDTO.toClazz();
        clazz.persist();

        //тук не връщаме body - реално като сме поствали сме знаели какво създаваме и няма смисъл да го връщаме
        return Response.created(URI.create(String.format("/%s/%d", CLAZZ_RESOURCE_PATH, clazz.id))).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ClazzDTO> getAllClazzes(@QueryParam("page") @DefaultValue("1") int page,
                                        @QueryParam("size") @DefaultValue("4") int size) {
        return Clazz.<Clazz>findAll()
                .page(page - 1, size)
                .stream()
                .map(ClazzDTO::new)
                .collect(Collectors.toList());
    }


    @GET
    @Path("name/{clazzName}")
    public Response getClazzByName(@PathParam("clazzName") @ClazzName String clazzName) {
        Pattern clazzPattern = Pattern.compile("(\\d+)([A-Z])+");
        Matcher clazzMatcher = clazzPattern.matcher(clazzName);
        String clazzNumber = "";
        String subclazzInitial = "";
        if (clazzMatcher.find()) {
            clazzNumber = clazzMatcher.group(1);
            subclazzInitial = clazzMatcher.group(2);
        }

        return Clazz.<Clazz>find("clazzNumber = ?1 and subclazzInitial = ?2", clazzNumber, subclazzInitial)
                .firstResultOptional()
                .map(ClazzDTO::new)
                .map(cdto -> Response.ok(cdto).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @PATCH
    @Transactional
    @Path("/{clazzId}/student")
    public Response addStudentToClazz(@PathParam("clazzId") @NotNull @Positive Long clazzId,
                                      @QueryParam("studentId") @NotNull @Positive Long studentId) {
        Clazz clazz = Clazz.findById(clazzId);
        Student student = Student.findById(studentId);

        //В Transactional минава през всичко - затова използваме if-else
        if (clazz == null || student == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else if (student.clazz != null) {
            //Check if student already has a clazz
            return Response.status(Response.Status.CONFLICT).entity(
                            Json.createObjectBuilder()
                                    .add("Conflict",
                                            String.format("For student with id %d there is already assigned klas with id %d", studentId, clazzId))
                                    .build()
                    )
                    .build();
        } else {
            student.clazz = clazz;
            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("teacher/{teacherId}")
    public Response getClazzesByTeacher(@PathParam("teacherId") @NotNull @Positive Long teacherId,
                                        @QueryParam("page") @DefaultValue("1") int page,
                                        @QueryParam("size") @DefaultValue("4") int size) {
        List<ClazzDTO> clazzes = Clazz.<Clazz>find("#" + GET_CLAZZES_BY_TEACHER, teacherId)
                .page(page - 1, size)
                .stream()
                .map(ClazzDTO::new)
                .collect(Collectors.toList());

        return Response.ok(clazzes).build();
    }


    @PATCH
    @Transactional
    @Path("{clazzId}/allowedsubjects")
    public Response assignSubjectToClazzAllowed(@PathParam("clazzId") @NotNull @Positive Long clazzId,
                                                @QueryParam("subjectId") @NotNull @Positive Long subjectId) {
        Clazz clazz = Clazz.findById(clazzId);
        Subject subject = Subject.findById(subjectId);

        if (clazz == null || subject == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            //if below relation exists in the db, we do not inform the user for a conflict, and also no new record is added in the below 1 intermediate table
            subject.clazzes.add(clazz); //add if absent such relation

            return Response.status(Response.Status.NO_CONTENT).build();
        }
    }

    @PATCH
    @Transactional
    @Path("{clazzId}/assign")
    public Response assignTeacherToClazzAndSubject(@PathParam("clazzId") @NotNull @Positive Long clazzId,
                                                   @QueryParam("teacherId") @NotNull @Positive Long teacherId,
                                                   @QueryParam("subjectId") @NotNull @Positive Long subjectId) {
        Clazz clazz = Clazz.findById(clazzId);
        Teacher teacher = Teacher.findById(teacherId);
        Subject subject = Subject.findById(subjectId);

        if (clazz == null || teacher == null || subject == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            //интегритет кой клас какви учебни предмети изобщо учи
            if (clazz.subjects.contains(subject)) {
                //subject.getClazzes().add(clazz);//!!!!тази проверка/запис не е необходима заради горния if!!!

                //if below 2 relations exist in the db, we do not inform the user for a conflict, and also no new record is added in the below 2 intermediate tables
                clazz.teachers.add(teacher); //add if absent such relation
                subject.teachers.add(teacher);//add if absent such relation

                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.CONFLICT).entity(
                        Json.createObjectBuilder()
                                .add("Conflict",
                                        String.format("Subject with id %d is not allowed to be taught in clazz with id %d", subjectId, clazzId))
                                .build()
                ).build();
            }
        }
    }

}
