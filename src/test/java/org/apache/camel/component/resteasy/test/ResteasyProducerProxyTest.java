package org.apache.camel.component.resteasy.test;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.component.resteasy.ResteasyComponent;
import org.apache.camel.component.resteasy.ResteasyConstants;
import org.apache.camel.component.resteasy.test.beans.Customer;
import org.apache.camel.component.resteasy.test.beans.CustomerList;
import org.apache.camel.component.resteasy.test.beans.CustomerService;
import org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface;
import org.apache.camel.spi.DataFormat;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : Roman Jakubco | rjakubco@redhat.com.
 */
@RunWith(Arquillian.class)
public class ResteasyProducerProxyTest extends CamelTestSupport {
    final static String URI = "http://localhost:8080" ;
    @Deployment
    public static WebArchive createDeployment() {

        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addAsWebInfResource(new File("src/test/resources/webWithoutAppContext.xml"), "web.xml")
                .addClasses(CustomerService.class, Customer.class, CustomerList.class, ProxyProducerInterface.class)
                .addPackage("org.apache.camel.component.resteasy")
                .addPackage("org.apache.camel.component.resteasy.servlet")
                .addAsLibraries(Maven.resolver().loadPomFromFile("src/test/resources/pom.xml").importRuntimeAndTestDependencies().resolve()
                        .withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-http:2.14.0").withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-test:2.14.0").withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.camel:camel-jackson:2.14.0").withTransitivity().asFile())
                .addAsLibraries(Maven.resolver().resolve("org.apache.commons:commons-lang3:3.3.2").withTransitivity().asFile());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {
                ResteasyComponent resteasy = new ResteasyComponent();
                CamelContext camelContext = getContext();
                camelContext.addComponent("resteasy", resteasy);


                DataFormat dataFormat = new JacksonDataFormat(Customer.class);


                from("direct:getAll").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=getAllCustomers");

                from("direct:get").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=getCustomer");

                from("direct:getUnmarshal").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=getCustomer").unmarshal(dataFormat);

                from("direct:post").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=createCustomer");

                from("direct:put").marshal(dataFormat).to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=updateCustomer");

                from("direct:delete").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=deleteCustomer");

                from("direct:moreAttributes").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=getSpecificThreeCustomers");
                from("direct:differentType").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=checkIfCustomerExists");

                from("direct:notResponseType").to("resteasy:" + URI + "?proxyClientClass=org.apache.camel.component.resteasy.test.beans.ProxyProducerInterface" +
                        "&proxyMethod=getCustomerWithoutResponse");


            }
        };
    }

    private void deleteCustomer(Integer id){
        Map<String, Object> headers = new HashMap<>();
        ArrayList<Object> params = new ArrayList<Object>();

        params.clear();
        headers.clear();
        params.add(id);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        String response = template.requestBodyAndHeaders("direct:delete", null, headers, String.class);
        Assert.assertTrue(response.contains(String.valueOf(id)));
        Assert.assertTrue(response.contains("Customer deleted :"));
    }

    @Test
    @InSequence(1)
    public void testProxyGetAll() throws Exception {
        String expectedUser1 = "{\"name\":\"Roman\",\"surname\":\"Jakubco\",\"id\":1}";
        String expectedUser2 = "{\"name\":\"Camel\",\"surname\":\"Rider\",\"id\":2}";

        String response = template.requestBody("direct:getAll", null, String.class);
        Assert.assertTrue(response.contains(expectedUser1));
        Assert.assertTrue(response.contains(expectedUser2));
    }

    @Test
    public void testProxyGet() throws Exception {
        String expectedBody = "{\"name\":\"Camel\",\"surname\":\"Rider\",\"id\":2}";

        Exchange response = template.request("direct:get", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                ArrayList<Object> params = new ArrayList<Object>();
                params.add(2);
                exchange.getIn().getHeaders().put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);
            }
        });
        Assert.assertEquals(expectedBody, response.getOut().getBody(String.class));
    }


    @Test
    public void testProxyGetUnmarshal() throws Exception {
        final Integer customerId = 2;
        Customer expectedCustomer = new Customer("Camel", "Rider", customerId);

        Exchange response = template.request("direct:getUnmarshal", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                ArrayList<Object> params = new ArrayList<Object>();
                params.add(2);

                exchange.getIn().getHeaders().put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);
            }
        });
        Assert.assertEquals(expectedCustomer, response.getOut().getBody(Customer.class));

    }

    @Test
    public void testProxyPost() throws Exception {
        Integer customerId = 3;
        Customer expectedCustomer = new Customer("TestPost", "TestPost", customerId);

        Map<String, Object> headers = new HashMap<>();
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(expectedCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        String response = template.requestBodyAndHeaders("direct:post", null, headers, String.class);
        Assert.assertEquals("Customer added : " + expectedCustomer, response);

        deleteCustomer(customerId);
    }

    @Test
    public void testProxyPut() throws Exception {
        Integer customerId = 4;
        Customer expectedCustomer = new Customer("TestPut", "TestPut", customerId);

        Map<String, Object> headers = new HashMap<>();
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(expectedCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        String response = template.requestBodyAndHeaders("direct:post", null, headers, String.class);
        Assert.assertEquals("Customer added : " + expectedCustomer, response);



        params.clear();
        headers.clear();
        expectedCustomer.setName("TestPutUpdated");
        expectedCustomer.setSurname("TestPutUpdated");
        params.add(expectedCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        response = template.requestBodyAndHeaders("direct:put", null, headers, String.class);
        Assert.assertEquals("Customer updated : " + expectedCustomer, response);

        deleteCustomer(customerId);
    }

    @Test
    public void testProxyDelete() throws Exception {
        Integer customerId = 5;
        Customer expectedCustomer = new Customer("TestDelete", "TestDelete", customerId);

        Map<String, Object> headers = new HashMap<>();
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(expectedCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        String response = template.requestBodyAndHeaders("direct:post", null, headers, String.class);
        Assert.assertEquals("Customer added : " + expectedCustomer, response);

        params.clear();
        headers.clear();
        params.add(customerId);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        response = template.requestBodyAndHeaders("direct:delete", null, headers, String.class);
        Assert.assertEquals("Customer deleted : " + expectedCustomer, response);

    }

    @Test
    public void testProxyCallWithMoreAttributes() throws Exception {
        Integer customerId = 6;
        Customer expectedCustomer = new Customer("Test", "Test", customerId);

        Map<String, Object> headers = new HashMap<>();
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(expectedCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        String response = template.requestBodyAndHeaders("direct:post", null, headers, String.class);
        Assert.assertEquals("Customer added : " + expectedCustomer, response);


        String expectedCustomers = "[{\"name\":\"Test\",\"surname\":\"Test\",\"id\":6},{\"name\":\"Camel\",\"surname\":\"Rider\",\"id\":2},{\"name\":\"Roman\",\"surname\":\"Jakubco\",\"id\":1}]";
        headers.clear();
        params.clear();
        params.add(6);
        params.add(2);
        params.add(1);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);
        response = template.requestBodyAndHeaders("direct:moreAttributes", null, headers, String.class);
        Assert.assertEquals(expectedCustomers, response);

        deleteCustomer(customerId);
    }

    @Test
    public void testProxyCallWithDifferentTypeAttributes() throws Exception {
        Integer customerId = 7;
        Customer expectedCustomer = new Customer("TestAttr", "TestAttr", customerId);

        Map<String, Object> headers = new HashMap<>();
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(expectedCustomer);

        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        String response = template.requestBodyAndHeaders("direct:post", null, headers, String.class);
        Assert.assertEquals("Customer added : " + expectedCustomer, response);


        headers.clear();
        params.clear();
        params.add(7);
        params.add(expectedCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);
        response = template.requestBodyAndHeaders("direct:differentType", null, headers, String.class);
        Assert.assertEquals("Customers are equal", response);

        headers.clear();
        params.clear();
        params.add(1);
        params.add(expectedCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);
        response = template.requestBodyAndHeaders("direct:differentType", null, headers, String.class);
        Assert.assertEquals("Customers are not equal", response);

        Customer testCustomer = new Customer("Camel", "Rider", 2);
        headers.clear();
        params.clear();
        params.add(2);
        params.add(testCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);
        response = template.requestBodyAndHeaders("direct:differentType", null, headers, String.class);
        Assert.assertEquals("Customers are equal", response);

        deleteCustomer(customerId);

    }

    @Test
    public void testProxyCallWithAttributesInWrongOrder() throws Exception {
        Integer customerId = 8;
        final Customer expectedCustomer = new Customer("TestWrong", "TestWrong", customerId);

        Map<String, Object> headers = new HashMap<>();
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(expectedCustomer);

        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        String response = template.requestBodyAndHeaders("direct:post", null, headers, String.class);
        Assert.assertEquals("Customer added : " + expectedCustomer, response);



        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);
        Exchange exchange = template.request("direct:differentType", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                ArrayList<Object> exchangeParams = new ArrayList<Object>();
                exchangeParams.add(expectedCustomer);
                exchangeParams.add(8);
                exchange.getIn().getHeaders().put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, exchangeParams);
            }
        });

        Assert.assertTrue(exchange.getOut().getBody() instanceof NoSuchMethodException);
        Assert.assertTrue(exchange.getOut().getHeaders().containsKey(ResteasyConstants.RESTEASY_PROXY_PRODUCER_EXCEPTION));

        deleteCustomer(customerId);
    }

    @Test
    public void testProxyCallOnMethodWithoutReturnTypeResponse() throws Exception {
        Integer customerId = 9;
        Customer expectedCustomer = new Customer("Test", "Test", customerId);

        Map<String, Object> headers = new HashMap<>();
        ArrayList<Object> params = new ArrayList<Object>();
        params.add(expectedCustomer);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);

        String response = template.requestBodyAndHeaders("direct:post", null, headers, String.class);
        Assert.assertEquals("Customer added : " + expectedCustomer, response);


        headers.clear();
        params.clear();
        params.add(9);
        headers.put(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, params);
        response = template.requestBodyAndHeaders("direct:notResponseType", null, headers, String.class);

        Assert.assertEquals(expectedCustomer.toString(), response);

        deleteCustomer(customerId);

    }

}
