package org.apache.camel.component.resteasy.test.beans;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
@Path("/proxy")
public interface ProxyServiceInterface {
    @GET
    @Consumes("text/plain")
    @Path("/get")
    public Response getProxy();


    @POST
    @Consumes("application/json")
    @Path("/createCustomer")
    public Response createCustomer(Customer customer);


}
