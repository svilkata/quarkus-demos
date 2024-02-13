package com.vidasoft.diary.absence;

import com.vidasoft.diary.model.Absence;
import com.vidasoft.diary.model.Clazz;
import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.model.Teacher;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.vidasoft.diary.absence.AbsenceResource.ABSENCE_RESOURCE_PATH;
import static com.vidasoft.diary.model.Subject.GET_SUBJECT_BY_SUBJECT_AND_TEACHER_AND_CLAZZ;
import static com.vidasoft.diary.model.Teacher.GET_TEACHERS_BY_SUBJECT_AND_CLAZZ;

@RequestScoped
@Path(ABSENCE_RESOURCE_PATH)
public class AbsenceResource {
    public static final String ABSENCE_RESOURCE_PATH = "absence";

    /*
    {
      "absenceHours": 2
    }
   */
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response teacherWritesAbsencePerStudentPerSubject(@QueryParam("studentId") @NotNull @Positive Long stId,
                                                             @QueryParam("subjectId") @NotNull @Positive Long subjId,
                                                             @QueryParam("teacherId") @NotNull @Positive Long teacherId,
                                                             @Valid @NotNull AbsenceDTO absenceDTO) {
        Student student = Student.findById(stId);
        Subject subject = Subject.findById(subjId);
        Teacher teacher = Teacher.findById(teacherId);

        Response responseNotFound = checkNotFoundTeacherStudentAndSubjectResponse(student, subject, teacher, stId, subjId, teacherId);
        if (responseNotFound != null) {
            return responseNotFound;
        }

        //Checkings for not founds.....
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

        //Checking if student has existing klas
        Clazz clazz = student.clazz;
        if (clazz == null) {
            jsonBuilder.add("Student", String.format("The Student with id %d does not have a clazz yet", stId));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        // Check if no assigned such subject and teacher to the clazz
        Subject subjectFound = Subject.<Subject>find("#" + GET_SUBJECT_BY_SUBJECT_AND_TEACHER_AND_CLAZZ, subjId, teacherId, clazz.id).firstResult();
        if (subjectFound == null) {
            jsonBuilder.add("Assignment", String.format("There is still no such assignment for subject %s and teacher %s for klas %s", subject, teacher, clazz));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        Absence absence = new Absence(absenceDTO.absenceHours, student, clazz, subject, teacher);
        absence.persist();
        AbsenceDTO absenceSavedDTO = new AbsenceDTO(absence);

        return Response.created(URI.create(String.format("/%s/%d", ABSENCE_RESOURCE_PATH, absenceSavedDTO.id))).entity(absenceSavedDTO).build();
    }

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAbsenceRecordsPerStudentPerSubject(@QueryParam("studentId") @NotNull @Positive Long stId,
                                                             @QueryParam("subjectId") @NotNull @Positive Long subjId,
                                                             @DefaultValue("1") @QueryParam("page") int page,
                                                             @DefaultValue("4") @QueryParam("size") int size) {
        Student student = Student.findById(stId);
        Subject subject = Subject.findById(subjId);

        Response responseNotFound = checkNotFoundStudentAndSubjectResponse(student, subject, stId, subjId);
        if (responseNotFound != null) {
            return responseNotFound;
        }

        //Checks for not founds.....
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

        //Checking if student has existing klas
        Clazz clazz = student.clazz;
        if (clazz == null) {
            jsonBuilder.add("Student", String.format("The student with id %d does not have a clazz yet", stId));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        // Check if no assigned such subject and teacher to the clazz, i.e. no teacher assigned yet, or any other relations not set
        // In case, new teacher comes to teach permanently or temporary clazz 6A on the same subject math
        List<Teacher> listTeachers = Teacher.<Teacher>find("#" + GET_TEACHERS_BY_SUBJECT_AND_CLAZZ, subjId, clazz.id).list();

        if (listTeachers.isEmpty()) {
            jsonBuilder.add("Assignment", String.format("There is still no such assignment for subject %s and clazz %s and/or no teacher assigned yet for sure", subject, clazz));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        List<Absence> allAbsenceRecordsFromAStudentAndASubjectOnePage =
                Absence.<Absence>find("student = ?1 AND subject = ?2 AND clazz = ?3", student, subject, clazz).page(page - 1, size).list();
        List<AbsenceDTO> absenceDTOs = allAbsenceRecordsFromAStudentAndASubjectOnePage.stream().map(absc -> new AbsenceDTO(absc)).collect(Collectors.toList());

//        //if list is empty, we return an empty array
        return Response.ok(absenceDTOs).build();
    }


    private Response checkNotFoundTeacherStudentAndSubjectResponse(Student student, Subject subject, Teacher teacher, Long stId, Long subjId, Long teacherId) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        //Check for not found
        if (student == null || subject == null || teacher == null) {
            if (student == null) {
                jsonBuilder.add("Student", String.format("No Student with id %d present", stId));
            }

            if (subject == null) {
                jsonBuilder.add("Subject", String.format("No subject with id %d present", subjId));
            }

            if (teacher == null) {
                jsonBuilder.add("Teacher", String.format("No Teacher with id %d present", teacherId));
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        return null;
    }

    private Response checkNotFoundStudentAndSubjectResponse(Student student, Subject subject, Long stId, Long subjId) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        //Check for not found
        if (student == null || subject == null) {
            if (student == null) {
                jsonBuilder.add("Student", String.format("No Student with id %d present", stId));
            }

            if (subject == null) {
                jsonBuilder.add("Subject", String.format("No subject with id %d present", subjId));
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        return null;
    }
}
