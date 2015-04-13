//package org.apache.camel.component.resteasy.test;
//
//import org.apache.camel.component.resteasy.test.beans.SimpleService;
//import org.apache.camel.component.resteasy.test.beans.TestBean;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.resteasy.client.jaxrs.ResteasyClient;
//import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
//import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.jboss.shrinkwrap.resolver.api.maven.Maven;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import javax.ws.rs.core.Response;
//import java.io.File;
//
///**
// * @author : roman on 21/03/15.
// */
//@RunWith(Arquillian.class)
//public class ResteasyBlueprintTest {
//    private final static String URI = "http://localhost:8080/test/simpleService/";
//
//    @Deployment
//    public static WebArchive createDeployment() {
//
//        return ShrinkWrap.create(WebArchive.class, "test.war")
//                .addAsResource(new File("src/test/resources/contexts/blueprint.xml"), "applicationContext.xml")
//                .addAsWebInfResource(new File("src/test/resources/webBlueprint.xml"), "web.xml")
//                .addClasses(SimpleService.class)
//                .addPackage("org.apache.camel.component.resteasy")
//                .addPackage("org.apache.camel.component.resteasy.servlet")
//                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
//                        .withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.aries.blueprint:org.apache.aries.blueprint.web:1.1.0").withTransitivity().asFile());
//    }
//
//    @Test
//    public void testMatchUriOnPrefix() throws Exception {
//        ResteasyClient client = new ResteasyClientBuilder().build();
//        ResteasyWebTarget target = client.target(URI + "getMsg2");
//        Response response = target.request().get();
//
//        Assert.assertEquals(200, response.getStatus());
//        Assert.assertEquals("Message from blueprint", response.readEntity(String.class));
//
//    }
//}
