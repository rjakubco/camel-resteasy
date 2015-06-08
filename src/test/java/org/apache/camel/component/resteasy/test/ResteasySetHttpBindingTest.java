package org.apache.camel.component.resteasy.test;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.resteasy.ResteasyComponent;
import org.apache.camel.component.resteasy.ResteasyHttpBinding;
import org.apache.camel.component.resteasy.test.beans.TestHttpBinding;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.impl.PropertyPlaceholderDelegateRegistry;
import org.apache.camel.spi.Registry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by roman on 25/04/15.
 */
@RunWith(Arquillian.class)
public class ResteasySetHttpBindingTest extends CamelTestSupport {

    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
//                .addAsResource(new File("src/test/resources/contexts/basicProducer.xml"), "applicationContext.xml")
                .addAsWebInfResource(new File("src/test/resources/webWithoutAppContext.xml"), "web.xml")
                .addClasses(TestHttpBinding.class)
                .addPackage("org.apache.camel.component.resteasy")
                .addPackage("org.apache.camel.component.resteasy.servlet")
                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
                        .withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-test:2.14.0").withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-jackson:2.14.0").withTransitivity().asFile());
    }


    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                ResteasyComponent resteasy = new ResteasyComponent();
                ResteasyHttpBinding httpBinding = new TestHttpBinding();
                CamelContext camelContext = getContext();

                Registry registry = context.getRegistry();
                if (registry instanceof PropertyPlaceholderDelegateRegistry)
                    registry = ((PropertyPlaceholderDelegateRegistry)registry).getRegistry();

                JndiRegistry jndiRegistry = (JndiRegistry) registry;
                jndiRegistry.bind("binding", httpBinding);
                camelContext.addComponent("resteasy", resteasy);

                from("direct:start").to("resteasy:http://www.google" +
                        ".com?resteasyMethod=GET&restEasyHttpBindingRef=#binding");
            }
        };
    }

    @Test
    public void testSettingResteasyHttpBinding() throws Exception {
        String response = template.requestBody("direct:start", null, String.class);
        Assert.assertEquals("Test from custom HttpBinding", response);

    }
}
