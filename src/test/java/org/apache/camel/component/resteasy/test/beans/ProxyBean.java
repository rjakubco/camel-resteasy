package org.apache.camel.component.resteasy.test.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public class ProxyBean implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        String customer = exchange.getIn().getBody(String.class);

        exchange.getOut().setBody("Customer added : " + customer);
        exchange.getOut().getHeaders().put("CamelHttpResponseCode", 200);
    }
}
