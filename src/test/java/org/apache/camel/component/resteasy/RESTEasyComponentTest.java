package org.apache.camel.component.resteasy;


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
                    .to("resteasy:http://localhost:8080/RESTfulExample/customer/print");
            }
        };
    }




    @Test
    public void testName() throws Exception {
        Exchange exchange = template.request("direct:start", null);
        System.out.println(exchange.getOut().getHeaders());
        Response test =  exchange.getOut().getHeader("RESTEASY_RESPONSE", Response.class);
//        System.out.println(test);
//        System.out.println(test.getHeaders());
//        System.out.println(test.getEntity());
//        System.out.println(test.getEntity());
        System.out.println(exchange.getOut().getBody(String.class));
    }
}


