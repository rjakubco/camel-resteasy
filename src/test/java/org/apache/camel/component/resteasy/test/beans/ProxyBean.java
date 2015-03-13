package org.apache.camel.component.resteasy.test.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Created by roman on 13/03/15.
 */
public class ProxyBean implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Customer customer = exchange.getIn().getBody(Customer.class);
        System.out.println(customer);
        exchange.getOut().setBody("Customer added : " + customer);
    }
}
