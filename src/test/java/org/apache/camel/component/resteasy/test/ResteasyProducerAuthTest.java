package org.apache.camel.component.resteasy.test;

import org.apache.camel.component.resteasy.test.beans.PrintService;
import org.apache.camel.component.resteasy.test.beans.TestBean;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by roman on 09/03/15.
 */
@RunWith(Arquillian.class)
public class ResteasyProducerAuthTest {
    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsResource(new File("src/test/resources/applicationContext.xml"))
                .addAsWebInfResource(new File("src/test/resources/web.xml"))
                .addClasses(PrintService.class, TestBean.class)
                .addPackage("org.apache.camel.component.resteasy")
                .addPackage("org.apache.camel.component.resteasy.servlet")
                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
                        .withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile());
    }
}
