package com.vida.webrest;

import com.vida.model.bind.StudentBindingModel;
import com.vida.model.entity.Student;
import com.vida.service.StudentService;

import javax.inject.Inject;
import javax.json.Json;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;
import java.util.Set;

@Path("/student")
public class StudentRestResource {
    @Inject
    StudentService studentService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewStudent(StudentBindingModel studentBindingModel) {
        //TODO: check the cases when incorrect input data
        return Response.ok(this.studentService.addNewStudentFromBindingModel(studentBindingModel)).build();
    }

    @GET()
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllStudents() {
        Set<Student> allStudents = this.studentService.getAllStudents();

        return allStudents.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("Comment", "No students available yet").build())
                .build()
                : Response.ok(allStudents).build();
    }

    @GET()
    @Path("/{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudentById(@PathParam("studentId") Long stId ) {
        Optional<Student> studentByIdOpt = this.studentService.getStudentById(stId);

        return studentByIdOpt.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("Comment", String.format("The student with id %d does not exist yet", stId)).build())
                .build()
                : Response.ok(studentByIdOpt.get()).location(URI.create("/student/" + stId)).build();
    }
}