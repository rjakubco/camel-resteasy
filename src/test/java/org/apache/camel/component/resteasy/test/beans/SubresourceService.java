package org.apache.camel.component.resteasy.test.beans;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Created by roman on 13/03/15.
 */
public class SubresourceService {
    @Path("/customers/{id}")
    public SubConsumer getCustomer(@PathParam("id") int id) {
        SubConsumer cust = null; // Find a customer object
        return cust;
    }



    public class SubConsumer {

        @GET
        public String get() {return null;}

        @Path("/address")
        public String getAddress() {return null;}

    }
}
