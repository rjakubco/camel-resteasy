package org.apache.camel.component.resteasy;

import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.impl.HeaderFilterStrategyComponent;
import org.apache.camel.impl.UriEndpointComponent;
import org.apache.camel.spi.RestConsumerFactory;
import org.apache.camel.util.URISupport;

import java.net.URI;
import java.util.Map;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RESTEasyComponent extends UriEndpointComponent implements RestConsumerFactory {
    private RESTEasyConfiguration configuration;
    private RESTEasyBinding binding;

    public RESTEasyComponent(){
        super(RESTEasyEndpoint.class);
    }



    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        RESTEasyEndpoint endpoint =  new RESTEasyEndpoint(remaining, this, configuration);
        setProperties(configuration, parameters);
        // construct URI so we can use it to get the splitted information
        URI u = new URI(remaining);
        String protocol = u.getScheme();

        String uriPattern = u.getPath();
        if (parameters.size() > 0) {
            uriPattern = uriPattern + "?" + URISupport.createQueryString(parameters);
        }

        int port = 0;
        String host = u.getHost();
        if (u.getPort() > 0) {
            port = u.getPort();
        }

        endpoint.setProtocol(protocol);
        endpoint.setUriPattern(uriPattern);
        endpoint.setHost(host);
        if (port > 0) {
            endpoint.setPort(port);
        }
        return
    }

    @Override
    public Consumer createConsumer(CamelContext camelContext, Processor processor, String verb, String basePath, String uriTemplate, String consumes, String produces, Map<String, Object> parameters) throws Exception {
        return null;
    }
}
