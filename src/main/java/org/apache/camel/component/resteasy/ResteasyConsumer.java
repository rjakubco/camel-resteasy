package org.apache.camel.component.resteasy;


import org.apache.camel.Processor;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.http.HttpEndpoint;


/**
 * A Consumer of exchanges for a service in Resteasy.  ResteasyConsumer acts a Resteasy
 * service to receive requests, convert them, and forward them to Camel
 * route for processing.
 *
 * @author : Roman Jakubco | rjakubco@redhat.com
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
