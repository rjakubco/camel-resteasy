package org.apache.camel.component.resteasy.test;

import org.apache.camel.component.resteasy.test.beans.*;
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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by roman on 09/03/15.
 */
@RunWith(Arquillian.class)
public class ResteasyConsumerTest {
    private final static String URI = "http://localhost:8080/test/customer/";

    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsResource(new File("src/test/resources/contexts/basicConsumer.xml"), "applicationContext.xml")
                .addAsWebInfResource(new File("src/test/resources/web.xml"))
                .addClasses(Customer.class, CustomerService.class, CustomerList.class)
                .addPackage("org.apache.camel.component.resteasy")
                .addPackage("org.apache.camel.component.resteasy.servlet")
                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
                        .withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile());
    }


    private Response createCustomer(Customer customer){
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "createCustomer");
        Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(customer, MediaType.APPLICATION_JSON_TYPE));

        Assert.assertEquals(200, response.getStatus());
        return response;
    }

    private Response deleteCustomer(int id){
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "deleteCustomer?id=" + id);
        Response response = target.request().delete();

        Assert.assertEquals(200, response.getStatus());

        return response;
    }

    private Customer getCustomer(int id){
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "getCustomer?id=" + id);
        Response response = target.request().get();

        Assert.assertEquals(200, response.getStatus());

        return response.readEntity(Customer.class);
    }

    @Test
    public void testGetAll() throws Exception {
        String expectedResponse = "[{\"name\":\"Roman\",\"surname\":\"Jakubco\",\"id\":1},{\"name\":\"Camel\",\"surname\":\"Rider\",\"id\":2}]";

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "getAll");
        Response response = target.request().get();

        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(expectedResponse, response.readEntity(String.class));

        File file = new File("target/test/consumerTest/all.txt");
        byte[] encoded = Files.readAllBytes(file.toPath());
        String responseBody = new String(encoded);

        Assert.assertEquals(expectedResponse, responseBody);
    }

    @Test
    public void testGet() throws Exception {
        Customer customer = getCustomer(2);

        Assert.assertEquals(new Customer("Camel", "Rider", 2), customer);

        File file = new File("target/test/consumerTest/get.txt");
        byte[] encoded = Files.readAllBytes(file.toPath());
        String responseBody = new String(encoded);

        Assert.assertEquals("{\"name\":\"Camel\",\"surname\":\"Rider\",\"id\":2}", responseBody);
    }

    @Test
    public void testPost() throws Exception {
        String expectedResponse = "Customer added : Customer{name='TestCreate', surname='TestCreate', id=3}";
        int customerId = 3;

        Customer customer = new Customer("TestCreate", "TestCreate", customerId);
        Response response = createCustomer(customer);

        Assert.assertEquals(expectedResponse, response.readEntity(String.class));

        File file = new File("target/test/consumerTest/create.txt");
        byte[] encoded = Files.readAllBytes(file.toPath());
        String responseBody = new String(encoded);
        Assert.assertEquals(expectedResponse, responseBody);

        Assert.assertEquals(customer, getCustomer(customerId));

        deleteCustomer(customerId);
    }

    @Test
    public void testDelete() throws Exception {
        String expectedResponse = "Customer deleted : Customer{name='TestDelete', surname='TestDelete', id=4}";
        int customerId = 4;

        Customer customer = new Customer("TestDelete", "TestDelete", customerId);

        createCustomer(customer);
        Response responseDelete = deleteCustomer(customerId);

        Assert.assertEquals(200, responseDelete.getStatus());
        Assert.assertEquals(expectedResponse, responseDelete.readEntity(String.class));

        File file = new File("target/test/consumerTest/delete.txt");
        byte[] encoded = Files.readAllBytes(file.toPath());
        String responseBody = new String(encoded);
        Assert.assertEquals(expectedResponse, responseBody);

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "getCustomer?id=" + customerId);
        Response response = target.request().get();

        Assert.assertEquals(404, response.getStatus());
        Assert.assertEquals("Customer with given id doesn't exist", response.readEntity(String.class));
    }

    @Test
    public void testPut() throws Exception {
        String expectedResponse = "Customer updated : Customer{name='TestPutUpdated', surname='TestPutUpdated', id=5}";
        int customerId = 5;

        Customer customer = new Customer("TestDelete", "TestDelete", customerId);

        createCustomer(customer);

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "updateCustomer");

        customer.setName("TestPutUpdated");
        customer.setSurname("TestPutUpdated");
        Response response = target.request(MediaType.APPLICATION_JSON_TYPE).put(Entity.entity(customer, MediaType.APPLICATION_JSON_TYPE));

        Assert.assertEquals(response.getStatus(), 200);
        Assert.assertEquals(expectedResponse, response.readEntity(String.class));

        File file = new File("target/test/consumerTest/update.txt");
        byte[] encoded = Files.readAllBytes(file.toPath());
        String responseBody = new String(encoded);
        Assert.assertEquals(expectedResponse, responseBody);

        Customer updatedCustomer = getCustomer(customerId);
        Assert.assertEquals(customer, updatedCustomer);

        deleteCustomer(customerId);
    }

    @Test
    public void testWrongMethod() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(URI + "createCustomer");
        Response response = target.request().get();

        Assert.assertEquals(405, response.getStatus());
    }


}
