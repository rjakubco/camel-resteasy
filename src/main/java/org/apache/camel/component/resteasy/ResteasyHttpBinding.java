package org.apache.camel.component.resteasy;

import org.apache.camel.Exchange;
import org.apache.camel.spi.HeaderFilterStrategy;

import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * Strategy interface for implementing binding between Resteasy and Camel
 *
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public interface ResteasyHttpBinding{


    /**
     * Populate Restlet request from Camel message
     *
     * @param exchange message to be copied from
     * @param response to be populated
     * @throws Exception is thrown if error processing
     */

    /**
     * Setter method for HeaderFilterStrategy
     *
     * @param headerFilterStrategy header filter strategy which should be used in ResteasyHttpBinding
     */
    public void setHeaderFilterStrategy(HeaderFilterStrategy headerFilterStrategy);

    /**
     * Populate Resteasy request from Camel exchange and execute it in Resteasy client
     *
     * @param uri URI used for client request
     * @param exchange message to be copied from
     * @param parameters to be used in Resteasy request configuration
     * @return response from the server to which was the request sent
     */
    public Response populateResteasyRequestFromExchangeAndExecute(String uri, Exchange exchange, Map<String, String> parameters);

    /**
     * Populate Resteasy request from Camel exchange and execute it as Resteasy proxy client
     *
     * @param uri URI used for client request
     * @param exchange message to be copied from
     * @param parameters  to be used in Resteasy request configuration
     */
    public void populateProxyResteasyRequestAndExecute(String uri, Exchange exchange, Map<String, String> parameters);

    /**
     * Populate Camel exchange from Resteasy response
     *
     * @param exchange to be populated
     * @param response message to be copied from
     */
    public void populateExchangeFromResteasyResponse(Exchange exchange, Response response);
}
