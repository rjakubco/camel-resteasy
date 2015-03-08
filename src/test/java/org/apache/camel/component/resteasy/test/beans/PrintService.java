package org.apache.camel.component.resteasy.test.beans;

import javax.servlet.ServletException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/customer")
public class PrintService {
//	CustomerBo customerBo;


    @GET
    @Path("/print")
    public Response printMessage() throws IOException, ServletException {

//        customerBo = (CustomerBo) SpringApplicationContext.getBean("customerBo");

        System.out.println("metoda z restu");
//        String result = customerBo.getMsg();

        return Response.status(200).entity("metoda z restu").build();
//        return
    }


    @GET
    @Path("/param")
    public Response printMessage(@QueryParam("id") String id) {

        String result = "Restful example : " + id;

        return Response.status(200).entity(result).build();

    }


    @GET
    @Path("/print2")
    public Response printMessage2(){
        return Response.status(200).entity("/customer/print2").build();
    }


    @GET
    @Path("/match/print3")
    public Response printMessage3(){
        return Response.status(200).entity("/customer/test/print2").build();
    }

//
//    @POST
//    @Path("/post")
//    @Consumes("application/json")
//    public Response createProductInJSON(Product product) {
//
//        String result = "Product created : " + product;
//        return Response.status(201).entity(result).build();
//
//    }
//
//    @GET
//    @Path("/get")
//    @Produces("application/json")
//    public Product getProductInJSON() {
//
//        Product product = new Product();
//        product.setName("iPad 3");
//        product.setQty(999);
//
//        return product;
//
//    }

}
