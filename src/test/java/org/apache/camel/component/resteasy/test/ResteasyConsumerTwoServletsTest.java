package org.apache.camel.component.resteasy.test;

import org.apache.camel.component.resteasy.test.beans.ServletOneService;
import org.apache.camel.component.resteasy.test.beans.ServletTwoService;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.File;

/**
* @author : Roman Jakubco (rjakubco@redhat.com).
*/
@RunWith(Arquillian.class)
public class ResteasyConsumerTwoServletsTest {
    private final static String URI = "http://localhost:8080/test/numberOne/";
    private final static String URI_2 = "http://localhost:8080/test/numberTwo/";

    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsResource(new File("src/test/resources/contexts/twoServlets.xml"), "applicationContext.xml")
                .addAsWebInfResource(new File("src/test/resources/webTwoServlets.xml"), "web.xml")
                .addClasses(ServletOneService.class, ServletTwoService.class)
                .addPackage("org.apache.camel.component.resteasy")
                .addPackage("org.apache.camel.component.resteasy.servlet")
                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
                        .withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile());
    }

    @Test
    public void testServletNameOption() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "simpleServiceSecure/getMsg");
        Response response = target.request().get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Message from camel-servlet-1", response.readEntity(String.class));

        ResteasyClient client2 = new ResteasyClientBuilder().build();
        ResteasyWebTarget target2 = client2.target(URI_2 + "simpleServiceSecure/getMsg");
        Response response2 = target2.request().get();

        Assert.assertEquals(200, response2.getStatus());
        Assert.assertEquals("Message from camel-servlet-2",response2.readEntity(String.class));



    }
}
