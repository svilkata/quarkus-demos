package com.vida.webrest;

import com.vida.model.bind.KlasBindingModel;
import com.vida.model.entity.Klas;
import com.vida.service.KlasService;

import javax.inject.Inject;
import javax.json.Json;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

@Path("/klas")
public class KlasRestResource {
    @Inject
    KlasService klasService;
    @Inject
    Validator validator;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewKlas(KlasBindingModel klasBindingModel) {
        Map<Integer, Klas> map = new HashMap<>();

        try {
            Set<ConstraintViolation<KlasBindingModel>> validate = validator.validate(klasBindingModel);
            if (!validate.isEmpty()) {
                return Response
                        .status(Response.Status.CONFLICT)
                        .entity(Json.createObjectBuilder()
                                .add("Comment", "Wrong input data - you can not create such a klas - verify the klas letter and the klas number")
                                .add("Wrong klas try", klasBindingModel.toString()).build()).build();
            }

            map = this.klasService.addNewKlasFromBindingModel(klasBindingModel);
        } catch (Exception e) {
            //TODO: what is the exact Exception here
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity(Json.createObjectBuilder()
                            .add("Comment", "Wrong input data - you can not create such a klas - verify the klas letter and the klas number")
                            .add("Wrong klas try", klasBindingModel.toString()).build())
                    .build();
        }

        Response response = null;
        if (map.containsKey(0)) {
            response = Response
                    .status(Response.Status.CONFLICT)
                    .entity(Json.createObjectBuilder()
                            .add("Comment", "Klas already exists in the database")
                            .add("The existing klas is", map.get(0).toString()).build())
                    .build();
        } else if (map.containsKey(1)) {
            response = Response.ok(map.get(1)).location(URI.create("/klas/" + map.get(1).getId())).build();
        } else if (map.containsKey(2)) {
            response = Response
                    .status(Response.Status.CONFLICT)
                    .entity(Json.createObjectBuilder()
                            .add("Comment", "Wrong input data - you can not create such a klass")
                            .add("Wrong klas try", klasBindingModel.toString()).build())
                    .build();
        }

        return response;
    }

    @GET()
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllKlassesLazy() {
        List<Klas> allKlasses = this.klasService.getAllKlassesLazy();

        return allKlasses.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("Comment", "No klasses available yet").build())
                .build()
                : Response.ok(allKlasses).build();
    }

    @GET()
    @Path("/{klasId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKlassByIdLazy(@PathParam("klasId") Long klasId) {
        Optional<Klas> klasOpt = this.klasService.getKlasByKlasIdLazy(klasId);

        return klasOpt.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("Comment", String.format("The klass with id %d does not exist yet", klasId)).build())
                .build()
                : Response.ok(klasOpt.get()).build();
    }

    @GET()
    @Path("/{klasId}/withstudents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getKlassByIdEagerWithStudents(@PathParam("klasId") Long klasId) {
        Optional<Klas> klasOpt = this.klasService.getKlasByKlasIdEager(klasId);

        return klasOpt.isEmpty()
                ? Response.status(Response.Status.NOT_FOUND)
                .entity(Json.createObjectBuilder()
                        .add("Comment", String.format("The klass with id %d does not exist yet", klasId)).build())
                .build()
                : Response.ok(klasOpt.get()).build();
    }

}
