package org.apache.camel.component.resteasy;

import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RESTEasyConsumer extends DefaultConsumer{
    public RESTEasyConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
}
