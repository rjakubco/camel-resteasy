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
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import java.io.File;
//
///**
//* Created by Roman Jakubco (rjakubco@redhat.com) on 09/03/15.
//*/
//@RunWith(Arquillian.class)
//public class ResteasyProducerProxyTest extends CamelTestSupport {
//    final static String URI = "http://localhost:8080" ;
//    @Deployment
//    public static WebArchive createDeployment() {
//
//        return ShrinkWrap.create(WebArchive.class, "test.war")
////                .addAsResource(new File("src/test/resources/contexts/applicationContext.xml"))
//                .addAsWebInfResource(new File("src/test/resources/web2.xml"), "web.xml")
//                .addClasses(CustomerService.class, Customer.class, CustomerList.class)
//                .addPackage("org.apache.camel.component.resteasy")
//                .addPackage("org.apache.camel.component.resteasy.servlet")
//                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
//                        .withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-test:2.14.0").withTransitivity().asFile());
//    }
//
//    protected RouteBuilder createRouteBuilder() {
//        return new RouteBuilder() {
//            public void configure() {
//                ResteasyComponent resteasy = new ResteasyComponent();
//                CamelContext camelContext = getContext();
//                camelContext.addComponent("resteasy", resteasy);
//
//                DataFormat dataFormat = new JacksonDataFormat(Customer.class);
//
//
//                from("direct:getAll").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=getAllCustomers");
//
//                from("direct:get").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=getCustomer");
//
//                from("direct:getUnmarshal").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=getCustomer").unmarshal(dataFormat);
//
//                from("direct:post").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=createCustomer");
//
//                from("direct:postInHeader").marshal(dataFormat).to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=createCustomer");
//
//                from("direct:postMarshal").marshal(dataFormat).to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=createCustomer");
//
//                from("direct:put").marshal(dataFormat).to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=updateCustomer");
//
//                from("direct:delete").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=deleteCustomer");
//
//                from("direct:moreAttributes").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=getSpecificThreeCustomers");
//
//                from("direct:notResponseType").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
//                        "&proxyMethod=getCustomerWithoutResponse");
//
//
//            }
//        };
//    }
//
//    @Test
//    public void testProxyGetAll() throws Exception {
//
//
//    }
//
//    @Test
//    public void testProxyGet() throws Exception {
//
//
//    }
//
//    @Test
//    public void testProxyPost() throws Exception {
//
//
//    }
//
//    @Test
//    public void testProxyPut() throws Exception {
//
//
//    }
//
//    @Test
//    public void testProxyDelete() throws Exception {
//
//
//    }
//
//    @Test
//    public void testProxyCallWithMoreAttributes() throws Exception {
//
//
//    }
//
//    @Test
//    public void testProxyCallOnMethodWithoutReturnTypeResponse() throws Exception {
//
//
//    }
//}
