package com.vida.subject;

import com.vida.model.Subject;
import com.vida.subject.dto.SubjectDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.json.Json;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.vida.subject.SubjectRestResource.SUBJECT_RESOURCE_PATH;

@Path(SUBJECT_RESOURCE_PATH)
public class SubjectRestResource {
    public static final String SUBJECT_RESOURCE_PATH = "/subject";

    Logger logger = LoggerFactory.getLogger(SubjectRestResource.class);

    @Inject
    SubjectService subjectService;
    @Inject
    SubjectMapStruct subjectMapStruct;

    @GET()
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSubjects(@DefaultValue("1") @QueryParam("page") int page,
                                   @DefaultValue("4") @QueryParam("size") int size) {
        List<SubjectDto> allTeachersDtosInPage = subjectService.getAllSubjects(page, size)
                .stream().map(subj -> subjectMapStruct.fromSubjectToSubjectDto(subj)).toList();

        //if list is empty, we return an empty array
        return Response.ok(allTeachersDtosInPage).build();
    }

    @GET()
    @Path("/{subjectId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubjectById(@PathParam("subjectId") @NotNull @Positive Long subjId) {
        Subject subject = subjectService.getSubjectById(subjId).orElse(null);

        if (subject == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Json.createObjectBuilder()
                            .add("Comment", String.format("No subject with id %d present", subjId))
                            .build())
                    .build();
        } else {
            return Response.ok(subjectMapStruct.fromSubjectToSubjectDto(subject)).build();
        }
    }

    /*
    {subject}
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response addNewSubject(@Valid @NotNull SubjectDto subjectDto) {
        Optional<Subject> subjectBySubjectOpt = subjectService.getSubjectBySubjectString(subjectDto.getSubject());

        if (subjectBySubjectOpt.isPresent()) {
            logger.info(String.format("Subject %s already exists in the database", subjectDto.getSubject()));

            return Response.status(Response.Status.CONFLICT)
                    .entity(Json.createObjectBuilder()
                            .add("Comment", String.format("Subject %s already exists in the database", subjectDto.getSubject()))
                            .build())
                    .build();
        }

        Subject subject = subjectMapStruct.fromSubjectDtoToSubject(subjectDto);
        Subject createdSubject = subjectService.addNewSubject(subject);

        return Response.ok(subjectMapStruct.fromSubjectToSubjectDto(createdSubject))
                .location(URI.create(SUBJECT_RESOURCE_PATH + "/" + createdSubject.getId())).build();
    }
}
