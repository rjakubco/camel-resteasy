//package org.apache.camel.component.resteasy.test;
//
//import org.apache.camel.CamelContext;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.component.jackson.JacksonDataFormat;
//import org.apache.camel.component.resteasy.ResteasyComponent;
//import org.apache.camel.component.resteasy.test.beans.*;
//import org.apache.camel.spi.DataFormat;
//import org.apache.camel.test.junit4.CamelTestSupport;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.jboss.shrinkwrap.resolver.api.maven.Maven;
//import org.junit.runner.RunWith;
//
//import java.io.File;
//
///**
//* @author : Roman Jakubco | rjakubco@redhat.com on 09/03/15.
//*/
//@RunWith(Arquillian.class)
//public class ResteasyProducerAuthTest extends CamelTestSupport {
//    final static String URI = "http://localhost:8080/test/simpleServiceSecure/" ;
//
//    @Deployment
//    public static WebArchive createDeployment() {
//
//        return ShrinkWrap.create(WebArchive.class, "test.war")
////                .addAsResource(new File("src/test/resources/applicationContext.xml"))
//                .addAsWebInfResource(new File("src/test/resources/webAuth.xml"), "web.xml")
//                .addClasses(SimpleServiceSecure.class)
//                .addPackage("org.apache.camel.component.resteasy")
//                .addPackage("org.apache.camel.component.resteasy.servlet")
//                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
//                        .withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-test:2.14.0").withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-jackson:2.14.0").withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.commons:commons-lang3:3.3.2").withTransitivity().asFile());
//    }
//
//    protected RouteBuilder createRouteBuilder() {
//        return new RouteBuilder() {
//            public void configure() {
//                ResteasyComponent resteasy = new ResteasyComponent();
//                CamelContext camelContext = getContext();
//                camelContext.addComponent("resteasy", resteasy);
//
//
//                DataFormat dataFormat = new JacksonDataFormat(Customer.class);
//
//
//                from("direct:getAll").to("resteasy:" + URI + "getMsg?username=");
//
////                from("direct:get").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
////                        "&proxyMethod=getCustomer");
////
////                from("direct:getUnmarshal").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
////                        "&proxyMethod=getCustomer").unmarshal(dataFormat);
////
////                from("direct:post").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
////                        "&proxyMethod=createCustomer");
////
////                from("direct:put").marshal(dataFormat).to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
////                        "&proxyMethod=updateCustomer");
////
////                from("direct:delete").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
////                        "&proxyMethod=deleteCustomer");
////
////                from("direct:moreAttributes").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
////                        "&proxyMethod=getSpecificThreeCustomers");
////                from("direct:differentType").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
////                        "&proxyMethod=checkIfCustomerExists");
////
////                from("direct:notResponseType").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
////                        "&proxyMethod=getCustomerWithoutResponse");
//
//
//            }
//        };
//    }
//}
