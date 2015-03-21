package org.apache.camel.component.resteasy.test.beans;

import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com).
 */
@Path("/customer")
public class CustomerService {
    private static CustomerList list;

    public CustomerService() {
        list = new CustomerList();
        list.add();
    }


    @HEAD
    @GET
    @Produces("application/json")
    @Path("/getAll")
    public Response getAllCustomers() throws IOException, ServletException {
       return Response.status(200).entity(list.getCustomerList()).build();
    }


    @GET
    @Produces("application/json")
    @Path("/getCustomer")
    public Response getCustomer(@QueryParam("id") Integer id) throws Exception {
        Customer c = list.getCustomer(id);
        if(c != null){
            return Response.status(200).entity(c).build();
        } else {
            return Response.status(404).entity("Customer with given id doesn't exist").build();
        }
    }


    @DELETE
    @Path("/deleteCustomer")
    public Response deleteCustomer(@QueryParam("id") Integer id) throws IOException, ServletException {
        Customer c = list.deleteCustomer(id);
        return Response.status(200).entity("Customer deleted : " + c ).build();
    }


    @POST
    @Consumes("application/json")
    @Path("/createCustomer")
    public Response createCustomer(Customer customer) throws IOException, ServletException {
        list.addCustomer(customer);
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


    /*
        Specific methods for servlets used in proxy producer test
     */
    // Really forced methods for testing proxy producer with more parameters
    @GET
    @Produces("application/json")
    @Path("/getSpecificThreeCustomers")
    public Response getSpecificThreeCustomers(@QueryParam("c1") Integer customerId1, @QueryParam("c2") Integer customerId2, @QueryParam("c3") Integer customerId3) throws Exception {
        List<Customer> customers = new ArrayList<>();
        customers.add(list.getCustomer(customerId1));
        customers.add(list.getCustomer(customerId2));
        customers.add(list.getCustomer(customerId3));

        return Response.status(200).entity(customers).build();
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("/checkCustomer")
    public Response checkIfCustomerExists(@QueryParam("c1") Integer customerId1, Customer customer) throws Exception {
        Customer foundCustomer = list.getCustomer(customerId1);
        if(foundCustomer.equals(customer)){
            return Response.status(200).entity("Customers are equal").build();
        } else{
            return Response.status(200).entity("Customers are not equal").build();
        }


    }

    @GET
    @Produces("application/json")
    @Path("/getCustomerWithoutResponse")
    public Customer getCustomerWithoutResponse(@QueryParam("c1") Integer customerId1) throws Exception {
        Customer c = list.getCustomer(customerId1);
        return c;
    }

}
