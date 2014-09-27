package org.apache.camel.component.resteasy.test;


import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import javax.ws.rs.core.Response;

/**
 * Created by reon on 29/08/14.
 */
public class RESTEasyComponentTest extends CamelTestSupport {
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start")
                    .to("resteasy:http://localhost:8080/RESTfulDemoApplication/user-management/users/1");
            }
        };
    }




    @Test
    public void testName() throws Exception {
        Exchange exchange = template.request("direct:start", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
//                exchange.getIn().setHeader(Exchange.HTTP_QUERY, "querytest=test1");
            }
        });
//        System.out.println(exchange.getOut().getHeaders());
        Response test =  exchange.getOut().getHeader("RESTEASY_RESPONSE", Response.class);
//        System.out.println(test);
//        System.out.println(test.getHeaders());
        // TODO: cannot get enertity from resteast_response because it is already closed. Not sure if ok or not.
        // because the entityt should be in body which can be retrieved
//        System.out.println(test.readEntity(String.class));
//        System.out.println(test.getEntity());

        String xx = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><user id=\"1\" uri=\"/user-management/users/1\"><firstName>demo</firstName><lastName>user</lastName></user>";
        System.out.println(exchange.getOut().getBody());
    }
}


