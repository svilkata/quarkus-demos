package com.vida.assignment;

import com.vida.assignment.dto.AssignTeacherSubjectAndKlasDto;
import com.vida.klas.KlasService;
import com.vida.model.Assignment;
import com.vida.model.Klas;
import com.vida.model.Subject;
import com.vida.model.Teacher;
import com.vida.subject.SubjectService;
import com.vida.teacher.TeacherService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

import static com.vida.assignment.AssignmentRestResource.ASSIGN_RESOURCE_PATH;


@Path(ASSIGN_RESOURCE_PATH)
public class AssignmentRestResource {
    public static final String ASSIGN_RESOURCE_PATH = "/assign";

    @Inject
    TeacherService teacherService;
    @Inject
    KlasService klasService;
    @Inject
    SubjectService subjectService;
    @Inject
    AssignmentService assignmentService;
    @Inject
    AssignmentMapStruct assignmentMapStruct;

    /*
    {
        "teacherId": 4,
        "subjectId": 3,
        "klasId": 7
    }
    */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response assignSubjectAndKlas(@Valid @NotNull AssignTeacherSubjectAndKlasDto assignTeacherSubjectAndKlasDto) {
        Long teacherId = assignTeacherSubjectAndKlasDto.getTeacherId();
        Long klasId = assignTeacherSubjectAndKlasDto.getKlasId();
        Long subjectId = assignTeacherSubjectAndKlasDto.getSubjectId();

        Teacher teacher = teacherService.getTeacherById(teacherId).orElse(null);
        Klas klas = klasService.getKlasById(klasId).orElse(null);
        Subject subject = subjectService.getSubjectById(subjectId).orElse(null);

        Response responseNotFound = checkNotFoundResponse(teacher, klas, subject, teacherId, klasId, subjectId);
        if (responseNotFound != null) {
            return responseNotFound;
        }

        Optional<Assignment> createdAssignment = assignmentService.assignATeacherASubjectAndAKlas(teacher, klas, subject);

        return createdAssignment
                .map(asgn -> assignmentMapStruct.fromAssignmentToAssignmentDto(asgn))
                .map(asngDto -> Response.ok(asngDto).location(URI.create(ASSIGN_RESOURCE_PATH + "/" + asngDto.getId())).build())

                .orElseGet(() -> Response.status(Response.Status.CONFLICT)
                        .entity(Json.createObjectBuilder()
                                .add("Comment", String.format("Assignment with teacherId %d, klasId %d and subjectId %d already exists in the database",
                                        teacherId, klasId, subjectId))
                                .build())
                        .build());
    }

    private Response checkNotFoundResponse(Teacher teacher, Klas klas, Subject subject, Long teacherId, Long klasId, Long subjectId) {
        JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        if (teacher == null || klas == null || subject == null) {
            if (teacher == null) {
                jsonBuilder.add("Teacher", String.format("No teacher with id %d present", teacherId));
            }

            if (klas == null) {
                jsonBuilder.add("Klas", String.format("No klas with id %d present", klasId));
            }

            if (subject == null) {
                jsonBuilder.add("Subject", String.format("No subject with id %d present", subjectId));
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonBuilder.build())
                    .build();
        }

        return null;
    }

}
