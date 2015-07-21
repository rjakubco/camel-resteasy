package org.apache.camel.component.resteasy.test;

import org.apache.camel.component.resteasy.test.beans.SimpleService;
import org.apache.camel.component.resteasy.test.beans.TestBean;
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
import java.nio.file.Files;

/**
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
@RunWith(Arquillian.class)
public class ResteasySimpleConsumerTest {
    private final static String URI = "http://localhost:8080/test/";

    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsResource(new File("src/test/resources/contexts/simpleConsumer.xml"), "applicationContext.xml")
                .addAsWebInfResource(new File("src/test/resources/web.xml"))
                .addClasses(SimpleService.class, TestBean.class)
                .addPackage("org.apache.camel.component.resteasy")
                .addPackage("org.apache.camel.component.resteasy.servlet")
                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
                        .withTransitivity().asFile());
//                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.16-SNAPSHOT").withTransitivity().asFile());
    }

    @Test
    public void testGettingResponseFromBean() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "simpleService/getMsg");
        Response response = target.request().get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Added this message from bean to original message from Rest Service -> Message1 from Rest service", response.readEntity(String.class));
    }

    @Test
    public void testGettingBodyFromCamelRoute() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "simpleService/getMsg2");
        Response response = target.request().get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Overriding output from Rest service", response.readEntity(String.class));
    }

    @Test
    public void testGettingResponseFromRestService() throws Exception {
        String expectedResponse = "Message3 from Rest service";

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "simpleService/getMsg3");
        Response response = target.request().get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(expectedResponse, response.readEntity(String.class));

        File file = new File("target/messageTest/response.txt");
        byte[] encoded = Files.readAllBytes(file.toPath());
        String responseBody = new String(encoded);

        Assert.assertEquals(expectedResponse, responseBody);
    }

}
