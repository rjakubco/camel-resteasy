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
//        System.out.println(exchange.getIn().getHeaders());
        ByteArrayOutputStream body = exchange.getIn().getBody(ByteArrayOutputStream.class);

        System.out.println("Body: " + body);
        String contentType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);

        String header = (String) exchange.getIn().getHeader("filter");
        System.out.println("header: " +  header);


        String path = exchange.getIn().getHeader(Exchange.HTTP_URI, String.class);
        HttpServletResponse e =  exchange.getIn().getHeader(Exchange.HTTP_SERVLET_RESPONSE, HttpServletResponse.class);

        String test = new String(body.toByteArray(), e.getCharacterEncoding());
        System.out.println("Test s streamom: " + test);



        HttpMessage message = exchange.getIn(HttpMessage.class);



//        exchange.getOut().setHeader(Exchange.CONTENT_TYPE, contentType + "; charset=UTF-8");
//        exchange.getOut().setHeader("PATH", path);
//        exchange.getOut().setBody("<b>Hello World</b>");
        exchange.getOut().setBody("Body z beany  a old body: " + body);
    }
}


