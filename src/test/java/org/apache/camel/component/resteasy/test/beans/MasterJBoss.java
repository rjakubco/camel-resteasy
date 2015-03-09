package org.apache.camel.component.resteasy.test.beans;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by roman on 17/11/14.
 */
@Path("/RESTfulExample/customer")
public interface MasterJBoss{
    @GET
    @Path("/print")
    public Response resteasyTutorial();


    @GET
    @Path("/param")
    public Response printMessage(@QueryParam("id") String id);
}
