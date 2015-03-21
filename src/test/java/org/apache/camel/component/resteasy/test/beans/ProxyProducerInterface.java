package org.apache.camel.component.resteasy.test.beans;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com).
 */
@Path("/test/customer")
public interface ProxyProducerInterface {


//    @HEAD
    @GET
    @Produces("application/json")
    @Consumes()
    @Path("/getAll")
    public Response getAllCustomers();

    @GET
    @Produces("application/json")
    @Path("/getCustomer")
    public Response getCustomer(@QueryParam("id") Integer id);

    @GET
    @Produces("application/json")
    @Path("/getSpecificThreeCustomers")
    public Response getSpecificThreeCustomers(@QueryParam("c1") Integer customerId1, @QueryParam("c2") Integer customerId2, @QueryParam("c3") Integer customerId3);

    @DELETE
    @Path("/deleteCustomer")
    public Response deleteCustomer(@QueryParam("id") Integer id);

    @POST
    @Consumes("application/json")
    @Path("/createCustomer")
    public Response createCustomer(Customer customer) ;

    @PUT
    @Consumes("application/json")
    @Path("/updateCustomer")
    public Response updateCustomer(Customer customer);

    @GET
    @Produces("application/json")
    @Path("/getCustomerWithoutResponse")
    public Customer getCustomerWithoutResponse(@QueryParam("c1") Integer customerId1);

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/checkCustomer")
    public Response checkIfCustomerExists(@QueryParam("c1") Integer customerId1, Customer customer);


}
