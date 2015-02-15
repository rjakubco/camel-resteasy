package org.apache.camel.component.resteasy;

import org.apache.camel.Exchange;
import org.apache.camel.component.http.DefaultHttpBinding;
import org.apache.camel.component.http.HttpBinding;

import javax.ws.rs.core.Response;
import java.util.Map;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public interface ResteasyHttpBinding{
    public Response populateResteasyRequestFromExchangeAndExecute(String uri, Exchange exchange, Map<String, String> parameters);

    public void populateProxyResteasyRequestAndExecute(String uri, Exchange exchange, Map<String, String> parameters);

    public void populateExchangeFromResteasyResponse(Exchange exchange, Response response);
}
