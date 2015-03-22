package org.apache.camel.component.resteasy.test.beans;

import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
@Path("/numberTwo/simpleServiceSecure")
public class ServletTwoService{
        @GET
        @Path("/getMsg")
        public Response getMessage() throws IOException, ServletException {
            return Response.status(200).entity("Message from camel-servlet-2").build();
        }

}
