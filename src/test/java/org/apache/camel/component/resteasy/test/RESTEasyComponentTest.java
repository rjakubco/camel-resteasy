//package org.apache.camel.component.resteasy.test;
//
//
//import org.apache.camel.component.resteasy.test.beans.SimpleService;
//import org.apache.camel.component.resteasy.test.beans.TestBean;
//
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
// * @author : reon on 29/08/14.
// */
//
//@RunWith(Arquillian.class)
//public class RESTEasyComponentTest {
//
//    @Deployment
//    public static WebArchive createDeployment() {
//
//        return ShrinkWrap.create(WebArchive.class, "test.war")
//                .addAsResource(new File("src/test/resources/applicationContext.xml"))
//                .addAsWebInfResource(new File("src/test/resources/web.xml"))
//                .addClasses(SimpleService.class, TestBean.class)
//                .addPackage("org.apache.camel.component.resteasy")
//                .addPackage("org.apache.camel.component.resteasy.servlet")
//                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
//                        .withTransitivity().asFile())
//                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile());
//    }
//
//
//    @Test
//    public void testName() throws Exception {
//        System.out.println("TEEEEEST");
//
//
//
//
//    }
//
//
////    @Test
////    public void testName() throws Exception {
////        Exchange exchange = template.request("direct:start", new Processor() {
////            @Override
////            public void process(Exchange exchange) throws Exception {
//////                exchange.getIn().setHeader(Exchange.HTTP_QUERY, "querytest=test1");
////            }
////        });
//////        System.out.println(exchange.getOut().getHeaders());
////        Response test =  exchange.getOut().getHeader("RESTEASY_RESPONSE", Response.class);
//////        System.out.println(test);
//////        System.out.println(test.getHeaders());
////
////        // because the entityt should be in body which can be retrieved
//////        System.out.println(test.readEntity(String.class));
//////        System.out.println(test.getEntity());
////
////        String xx = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><user id=\"1\" uri=\"/user-management/users/1\"><firstName>demo</firstName><lastName>user</lastName></user>";
////        Assert.assertNotNull(exchange.getOut().getBody());
////        Assert.assertEquals(xx, exchange.getOut().getBody());
////    }
//}
//
//
