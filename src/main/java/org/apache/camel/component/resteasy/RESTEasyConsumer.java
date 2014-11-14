package org.apache.camel.component.resteasy;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.http.HttpEndpoint;
import org.apache.camel.impl.DefaultConsumer;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import java.io.IOException;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RESTEasyConsumer extends HttpConsumer {
    public RESTEasyConsumer(HttpEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    public RESTEasyEndpoint getEndpoint() {
        return (RESTEasyEndpoint) super.getEndpoint();
    }



}
