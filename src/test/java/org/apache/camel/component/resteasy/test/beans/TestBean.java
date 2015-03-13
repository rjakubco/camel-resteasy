package org.apache.camel.component.resteasy.test.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpMessage;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;


/**
 * Created by roman on 17/10/14.
 */
public class TestBean implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        ByteArrayOutputStream body = exchange.getIn().getBody(ByteArrayOutputStream.class);

//        String contentType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);
//
//        String header = (String) exchange.getIn().getHeader("filter");
//
//
//
//        String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
//        HttpServletResponse e =  exchange.getIn().getHeader(Exchange.HTTP_SERVLET_RESPONSE, HttpServletResponse.class);

//        String test = new String(body.toByteArray(), e.getCharacterEncoding());
        exchange.getOut().setBody("Added this message from bean to original message from Rest Service -> " + body);
    }
}


