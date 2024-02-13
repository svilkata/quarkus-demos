package com.vidasoft.diary.subject;

import com.vidasoft.diary.model.Subject;
import com.vidasoft.diary.validation.SubjectName;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.vidasoft.diary.subject.SubjectResource.SUBJECT_RESOURCE_PATH;

@RequestScoped
@Path(SUBJECT_RESOURCE_PATH)
public class SubjectResource {
    public static final String SUBJECT_RESOURCE_PATH = "subject";

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSubject(@Valid @NotNull SubjectDTO subjectDTO) {
        Subject subject = subjectDTO.toSubject();
        subject.persist();

        return Response.created(URI.create(String.format("/%s/%d", SUBJECT_RESOURCE_PATH, subject.id))).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SubjectDTO> getAllSubjects(@QueryParam("page") @DefaultValue("1") int page,
                                           @QueryParam("size") @DefaultValue("4") int size) {
        return Subject.<Subject>findAll()
                .page(page - 1, size)
                .stream()
                .map(SubjectDTO::new)
                .collect(Collectors.toList());
    }


    @GET
    @Path("/name/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public SubjectDTO getSubjectByName(@PathParam("name") @SubjectName String subjectName) {
        return Subject.<Subject>find("name", subjectName)
                .firstResultOptional()
                .map(SubjectDTO::new)
                .orElseThrow(() -> new NotFoundException());
    }
}
