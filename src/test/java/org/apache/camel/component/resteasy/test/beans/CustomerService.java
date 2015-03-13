package org.apache.camel.component.resteasy.test.beans;

import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 13/03/15.
 */
@Path("/customer")
public class CustomerService {
    private static CustomerList list;

    public CustomerService() {
        list = new CustomerList();
        list.add();
    }

    @GET
    @Produces("application/json")
    @Path("/getAll")
    public Response getAllCustomers() throws IOException, ServletException {
       return Response.status(200).entity(list.getCustomerList()).build();
    }

    @GET
    @Produces("application/json")
    @Path("/getCustomer")
    public Response getCustomer(@QueryParam("id") int id) throws Exception {
        Customer c = list.getCustomer(id);
        if(c != null){
            return Response.status(200).entity(c).build();
        } else {
            return Response.status(404).entity("Customer with given id doesn't exist").build();
        }
    }

    @DELETE
    @Path("/deleteCustomer")
    public Response deleteCustomer(@QueryParam("id") int id) throws IOException, ServletException {
        Customer c = list.deleteCustomer(id);
        return Response.status(200).entity("Customer deleted : " + c ).build();
    }


    @POST
    @Consumes("application/json")
    @Path("/createCustomer")
    public Response createCustomer(Customer customer) throws IOException, ServletException {
        list.addCustomer(customer);
        System.out.println(list.getCustomerList());
        return Response.status(200).entity("Customer added : " + customer).build();
    }

    @PUT
    @Consumes("application/json")
    @Path("/updateCustomer")
    public Response updateCustomer(Customer customer) throws Exception {
        Customer update = list.getCustomer(customer.getId());
        if(update != null){
            list.deleteCustomer(customer.getId());
            list.addCustomer(customer);
            return Response.status(200).entity("Customer updated : "  + customer).build();
        } else{
            return Response.status(404).entity("Customer with given id doesn't exist").build();
        }



    }


}
