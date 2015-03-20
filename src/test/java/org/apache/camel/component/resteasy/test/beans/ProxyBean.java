package org.apache.camel.component.resteasy.test.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Roman Jakubco (rjakubco@redhat.com) on 13/03/15.
 */
public class ProxyBean implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String customer = exchange.getIn().getBody(String.class);

        exchange.getOut().setBody("Customer added : " + customer);
        exchange.getOut().getHeaders().put("CamelHttpResponseCode", 200);
    }
}
