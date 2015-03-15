package org.apache.camel.component.resteasy.test.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by Roman Jakubco (rjakubco@redhat.com) on 13/03/15.
 */
public class ProxyBean implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        // TODO this set up doesnt work
        Customer customer = exchange.getIn().getBody(Customer.class);
        String string = exchange.getIn().getBody(String.class);
        System.out.println(string);
        System.out.println("customer " + customer);
        exchange.getOut().setBody("Customer added : " + string);
        exchange.getOut().getHeaders().put("CamelHttpResponseCode", 200);
    }
}
