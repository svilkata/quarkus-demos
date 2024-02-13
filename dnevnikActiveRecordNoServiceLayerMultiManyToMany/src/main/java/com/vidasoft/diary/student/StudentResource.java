package com.vidasoft.diary.student;

import com.vidasoft.diary.model.Student;
import com.vidasoft.diary.validation.Identity;

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

import static com.vidasoft.diary.student.StudentResource.STUDENT_RESOURCE_PATH;

@RequestScoped
@Path(STUDENT_RESOURCE_PATH)
public class StudentResource {
    public static final String STUDENT_RESOURCE_PATH = "student";

    /*
    {
        "firstName": "Baj",
        "lastName": "Ganio",
        "identity": "2222222220"
    }
     */
    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createStudent(@Valid @NotNull StudentDTO studentDTO) {
        Student student = studentDTO.toStudent();
        student.persist();

        return Response.created(URI.create(String.format("/%s/%d", STUDENT_RESOURCE_PATH, student.id))).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<StudentDTO> getAllStudents(@QueryParam("page") @DefaultValue("1") int page,
                                           @QueryParam("size") @DefaultValue("4") int size) {
        return Student.<Student>findAll()
                .page(page - 1, size)
                .stream()
                .map(StudentDTO::new)
                .collect(Collectors.toList());
    }


    @GET
    @Path("identity/{identity}")
    @Produces(MediaType.APPLICATION_JSON)
    public StudentDTO getStudentByIdentity(@PathParam("identity") @Identity String identity) {
        return Student.<Student>find("identity", identity)
                .firstResultOptional()
                .map(StudentDTO::new)
                .orElseThrow(() -> new NotFoundException());
    }

}
