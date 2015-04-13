package org.apache.camel.component.resteasy.test.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.ByteArrayOutputStream;


/**
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public class TestBean implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        ByteArrayOutputStream body = exchange.getIn().getBody(ByteArrayOutputStream.class);
        exchange.getOut().setBody("Added this message from bean to original message from Rest Service -> " + body);
    }
}


