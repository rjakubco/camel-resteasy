package org.apache.camel.component.resteasy;

import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.http.HttpEndpoint;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class ResteasyConsumer extends HttpConsumer {
    public ResteasyConsumer(HttpEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    public ResteasyEndpoint getEndpoint() {
        return (ResteasyEndpoint) super.getEndpoint();
    }



}
