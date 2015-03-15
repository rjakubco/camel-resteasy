package org.apache.camel.component.resteasy.test.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpMessage;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;


/**
 * Created by Roman Jakubco (rjakubco@redhat.com) on 17/10/14.
 */
public class TestBean implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        ByteArrayOutputStream body = exchange.getIn().getBody(ByteArrayOutputStream.class);
        exchange.getOut().setBody("Added this message from bean to original message from Rest Service -> " + body);
    }
}


