package org.apache.camel.component.resteasy.test.beans;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by roman on 27/10/14.
 */
@Path("/test")
public interface ServiceInterface {

    @GET
    @Path("/print")
    public Response printMessage3();

    @GET
    @Path("/match/print")
    public Response printMessage();

    @POST
    @Path("/post")
    @Consumes("application/json")
    public Response createProductInJSON(Product product);
}

