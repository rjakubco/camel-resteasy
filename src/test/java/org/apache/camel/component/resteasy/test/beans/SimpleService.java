package org.apache.camel.component.resteasy.test.beans;

import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/simpleService")
public class SimpleService {
//	CustomerBo customerBo;


    @GET
    @Path("/getMsg")
    public Response getMessage() throws IOException, ServletException {
        return Response.status(200).entity("Message1 from Rest service").build();
    }

    @GET
    @Path("/getMsg2")
    public Response getMessage2() throws IOException, ServletException {
        return Response.status(200).entity("Message2 from Rest service").build();
    }

    @GET
    @Path("/getMsg3")
    public Response getMessage3() throws IOException, ServletException {
        return Response.status(200).entity("Message3 from Rest service").build();
    }


    @GET
    @Path("/match/prefix")
    public Response matchOnUri() throws IOException, ServletException {
        return Response.status(200).entity("Prefix").build();
    }








}
