package org.apache.camel.component.resteasy.test.beans;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by roman on 20/03/15.
 */
@Path("/numberOne/simpleServiceSecure")
public class ServletOneService {

    @GET
    @Path("/getMsg")
    public Response getMessage() throws IOException, ServletException {
        return Response.status(200).entity("Message from camel-servlet-1").build();
    }

}
