package org.apache.camel.component.resteasy.test.beans;

import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by Roman Jakubco (rjakubco@redhat.com) on 17/11/14.
 */
@Path("/proxy")
public interface ProxyServiceInterface {
    @GET
    @Path("/get")
    public Response getProxy();


    @POST
    @Consumes("application/json")
    @Path("/createCustomer")
    public Response createCustomer(Customer customer);


}
