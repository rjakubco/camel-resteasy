package org.apache.camel.component.resteasy;

import org.apache.camel.Exchange;
import org.apache.camel.spi.HeaderFilterStrategy;

import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Strategy interface for implementing binding between Resteasy and Camel
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public interface ResteasyHttpBinding{

    /**
     *
     * @param headerFilterStrategy
     */
    public void setHeaderFilterStrategy(HeaderFilterStrategy headerFilterStrategy);

    /**
     *
     * @param uri
     * @param exchange
     * @param parameters
     * @return
     */
    public Response populateResteasyRequestFromExchangeAndExecute(String uri, Exchange exchange, Map<String, String> parameters);

    /**
     *
     * @param uri
     * @param exchange
     * @param parameters
     */
    public void populateProxyResteasyRequestAndExecute(String uri, Exchange exchange, Map<String, String> parameters);

    /**
     *
     * @param exchange
     * @param response
     */
    public void populateExchangeFromResteasyResponse(Exchange exchange, Response response);
}
