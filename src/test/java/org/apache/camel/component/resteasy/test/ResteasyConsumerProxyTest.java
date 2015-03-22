package org.apache.camel.component.resteasy.test;

import org.apache.camel.component.resteasy.test.beans.Customer;
import org.apache.camel.component.resteasy.test.beans.ProxyBean;
import org.apache.camel.component.resteasy.test.beans.ProxyServiceInterface;
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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;

/**
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
@RunWith(Arquillian.class)
public class ResteasyConsumerProxyTest {

    private final static String URI = "http://localhost:8080/test/";

    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsResource(new File("src/test/resources/contexts/consumerProxy.xml"), "applicationContext.xml")
                .addAsWebInfResource(new File("src/test/resources/web.xml"))
                .addClasses(ProxyServiceInterface.class, ProxyBean.class, Customer.class)
                .addPackage("org.apache.camel.component.resteasy")
                .addPackage("org.apache.camel.component.resteasy.servlet")
                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
                        .withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile())
        .addAsLibraries(Maven.resolver().resolve("commons-io:commons-io:2.4").withTransitivity().asFile());
    }


    @Test
    public void testProxyOnlyFromCamel() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "camel/address");
        Response response = target.request().get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Proxy address only from Camel", response.readEntity(String.class));

    }

    @Test
    public void testProxyFromInterface() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "proxy/get");
        Response response = target.request().get();


        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Address from ProxyInterface", response.readEntity(String.class));

    }

    // TODO using bean in camel route a getting body as Customer.class doesn't work -> need to investigate
    @Test
    public void testProxyPostFromInterface() throws Exception {
        Customer customer = new Customer("Camel", "Rider", 1);

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "proxy/createCustomer");
        Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(customer, MediaType.APPLICATION_JSON_TYPE));

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Customer added : {\"name\":\"Camel\",\"surname\":\"Rider\",\"id\":1}", response.readEntity(String.class));

    }

    @Test
    public void testWrongMethodOnProxyInterface() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "proxy/createCustomer");
        Response response = target.request().get();

        Assert.assertEquals(405, response.getStatus());

    }
}
