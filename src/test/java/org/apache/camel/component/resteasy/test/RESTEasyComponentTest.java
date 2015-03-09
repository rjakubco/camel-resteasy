package org.apache.camel.component.resteasy.test;


import com.google.inject.Inject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.component.resteasy.test.beans.PrintService;
import org.apache.camel.component.resteasy.test.beans.TestBean;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.exporter.zip.ZipExporterImpl;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;

/**
 * Created by reon on 29/08/14.
 */

@RunWith(Arquillian.class)
public class RESTEasyComponentTest {
    WebArchive test;

    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "superBalik.war")
                .addAsResource(new File("src/test/resources/applicationContext.xml"))
                .addAsWebInfResource(new File("src/test/resources/web.xml"))
//                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
//                .addAsWebInfResource(new File("src/test/resources/jboss-web.xml"))
                .addClasses(PrintService.class, TestBean.class)
                .addPackage("org.apache.camel.component.resteasy")
                .addPackage("org.apache.camel.component.resteasy.servlet")
                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
                        .withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile());



//        return ShrinkWrap.create(MavenImporter.class)
//                .loadPomFromFile("src/test/resources/pom.xml").importBuildOutput().as(WebArchive.class).addAsResource(new File("src/test/resources/applicationContext.xml"))
//                .setWebXML(new File("src/test/resources/web.xml"))
//                .addClasses(PrintService.class, TestBean.class).addPackage("org.apache.camel.component.resteasy")
//                .addPackage("org.apache.camel.component.resteasy.servlet").addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile());

    }


    @Test
    public void testName() throws Exception {
        System.out.println("TEEEEEST");




    }


//    @Test
//    public void testName() throws Exception {
//        Exchange exchange = template.request("direct:start", new Processor() {
//            @Override
//            public void process(Exchange exchange) throws Exception {
////                exchange.getIn().setHeader(Exchange.HTTP_QUERY, "querytest=test1");
//            }
//        });
////        System.out.println(exchange.getOut().getHeaders());
//        Response test =  exchange.getOut().getHeader("RESTEASY_RESPONSE", Response.class);
////        System.out.println(test);
////        System.out.println(test.getHeaders());
//
//        // because the entityt should be in body which can be retrieved
////        System.out.println(test.readEntity(String.class));
////        System.out.println(test.getEntity());
//
//        String xx = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><user id=\"1\" uri=\"/user-management/users/1\"><firstName>demo</firstName><lastName>user</lastName></user>";
//        Assert.assertNotNull(exchange.getOut().getBody());
//        Assert.assertEquals(xx, exchange.getOut().getBody());
//    }
}


