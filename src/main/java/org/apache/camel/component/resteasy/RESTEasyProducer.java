package org.apache.camel.component.resteasy;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RESTEasyProducer extends DefaultProducer {
    public RESTEasyProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {

    }
}
