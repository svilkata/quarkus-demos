package com.vida.mark;

import com.vida.assignment.AssignmentService;
import com.vida.mark.dto.MarkDto;
import com.vida.model.*;
import com.vida.student.StudentService;
import com.vida.subject.SubjectService;
import com.vida.teacher.TeacherService;

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
import java.util.List;

import static com.vida.mark.MarkRestResource.MARKS_RESOURCE_PATH;

@Path(MARKS_RESOURCE_PATH)
public class MarkRestResource {
    public static final String MARKS_RESOURCE_PATH = "/mark";

    @Inject
    MarksService marksService;
    @Inject
    StudentService studentService;
    @Inject
    SubjectService subjectService;
    @Inject
    AssignmentService assignmentService;
    @Inject
    TeacherService teacherService;


    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{studentId}/{subjectId}")
    public Response getAllMarksPerStudentPerSubject(@PathParam("studentId") @NotNull @Positive Long stId,
                                                    @PathParam("subjectId") @NotNull @Positive Long subjId,
                                                    @DefaultValue("1") @QueryParam("page") int page,
                                                    @DefaultValue("4") @QueryParam("size") int size) {
        Student student = studentService.getStudentById(stId).orElse(null);
        Subject subject = subjectService.getSubjectById(subjId).orElse(null);

        Response responseNotFound = checkNotFoundStudentAndSubjectResponse(student, subject, stId, subjId);
        if (responseNotFound != null) {
            return responseNotFound;
        }

        //Checks for not founds.....
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

        //Checking if student has existing klas
        Klas klas = student.getKlas();
        if (klas == null) {
            jsonBuilder.add("Student", String.format("The student with id %d does not have a klas", stId));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        // Check if no assigned such subject to the klas
        // In case, new teacher comes to teach permanently or temporary klas 1A on the same object math
        List<Assignment> assignmentsForSubjectAndKlasOfStudent = assignmentService.getAssignmentsForSubjectAndKlasOfStudent(subject, klas);
        if (assignmentsForSubjectAndKlasOfStudent.isEmpty()) {
            jsonBuilder.add("Assignment", String.format("There is still no such assignment for subject %s and klas %s", subject, klas));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        List<Mark> allMarksFromAStudentAndASubjectOnePage = marksService.getAllMarksFromAStudentAndASubject(student, assignmentsForSubjectAndKlasOfStudent, page, size);
        List<MarkDto> markDtos = allMarksFromAStudentAndASubjectOnePage.stream().map(mrk -> new MarkDto(mrk)).toList();

//        //if list is empty, we return an empty array
        return Response.ok(markDtos).build();
    }


    /*
    {
        "mark": 5,
        "teacherId": 1
    }
    */
    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{studentId}/{subjectId}")
    @Transactional
    public Response teacherWriteMarkPerStudentPerSubject(@PathParam("studentId") @NotNull @Positive Long stId,
                                                         @PathParam("subjectId") @NotNull @Positive Long subjId,
                                                         @Valid @NotNull MarkDto markDto) {
        Teacher teacher = teacherService.getTeacherById(markDto.getTeacherId()).orElse(null);
        Student student = studentService.getStudentById(stId).orElse(null);
        Subject subject = subjectService.getSubjectById(subjId).orElse(null);

        Response responseNotFound = checkNotFoundTeacherStudentAndSubjectResponse(teacher, student, subject, markDto.getTeacherId(), stId, subjId);
        if (responseNotFound != null) {
            return responseNotFound;
        }

        //Checkings for not founds.....
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();

        //Checking if student has existing klas
        Klas klas = student.getKlas();
        if (klas == null) {
            jsonBuilder.add("Student", String.format("The Student with id %d does not have a klas", stId));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        // Check if no assigned such subject and teacher to the klas
        Assignment assignmentForSubjectTeacherAndKlas = assignmentService.getAssignmentForSubjectTeacherAndKlasOfStudent(subject, klas, teacher).orElse(null);
        if (assignmentForSubjectTeacherAndKlas == null) {
            jsonBuilder.add("Assignment", String.format("There is still no such assignment for subject %s and teacher %s for klas %s", subject, teacher, klas));
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        MarkDto markSavedDto = new MarkDto(marksService.writeMarkPerStudentPerSubject(student, assignmentForSubjectTeacherAndKlas, markDto.getMark()));

        return Response.ok(markSavedDto).build();
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

    private Response checkNotFoundTeacherStudentAndSubjectResponse(Teacher teacher, Student student, Subject subject, Long teachId, Long stId, Long subjId) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        //Check for not found
        if (teacher == null || student == null || subject == null) {
            if (teacher == null) {
                jsonBuilder.add("Teacher", String.format("No Teacher with id %d present", teachId));
            }

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
