package org.apache.camel.component.resteasy;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.util.MessageHelper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RESTEasyProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(RESTEasyProducer.class);
    private ResteasyClient client;



    public RESTEasyProducer(Endpoint endpoint) {
        super(endpoint);
        client = new ResteasyClientBuilder().build();

    }


    @Override
    public void process(Exchange exchange) throws Exception {
            RESTEasyEndpoint endpoint = (RESTEasyEndpoint) getEndpoint();

//            final RestletBinding binding = endpoint.getRestletBinding();
            Request request;

//            try {
                String uri = endpoint.getProtocol() + "://" + endpoint.getHost() + ":" + endpoint.getPort() + endpoint.getUriPattern();
//                String resourceUri = buildUri(endpoint, exchange);
        System.out.println(uri);
                ResteasyWebTarget target = client.target(uri);
                Response response = target.request().get();

//        System.out.println(response.readEntity(String.class));
                populateExchangeFromRestletResponse(exchange, response);



//                binding.populateRestletRequestFromExchange(request, exchange);
//            } catch (CamelExchangeException e) {
//                // break out in case of exception
//                exchange.setException(e);
////                callback.done(true);
////                return true;
//            }
    }

//    private static String buildUri(RESTEasyEndpoint endpoint, Exchange exchange) throws CamelExchangeException {
//        String uri = endpoint.getProtocol() + "://" + endpoint.getHost() + ":" + endpoint.getPort() + endpoint.getUriPattern();
//
//        // substitute { } placeholders in uri and use mandatory headers
////        LOG.trace("Substituting '(value)' placeholders in uri: {}", uri);
//        Matcher matcher = PATTERN.matcher(uri);
//        while (matcher.find()) {
//            String key = matcher.group(1);
//            String header = exchange.getIn().getHeader(key, String.class);
//            // header should be mandatory
//            if (header == null) {
//                throw new CamelExchangeException("Header with key: " + key + " not found in Exchange", exchange);
//            }
//
////            if (LOG.isTraceEnabled()) {
////                LOG.trace("Replacing: {} with header value: {}", matcher.group(0), header);
////            }
//
//            uri = matcher.replaceFirst(header);
//            // we replaced uri so reset and go again
//            matcher.reset(uri);
//        }
//
////        String query = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
////        if (query != null) {
//////            LOG.trace("Adding query: {} to uri: {}", query, uri);
////            uri = addQueryToUri(uri, query);
////        }
//
////        LOG.trace("Using uri: {}", uri);
//        return uri;
//    }


//    @Override
//    public void doStart() throws Exception {
//        super.doStart();
//        client.start();
//    }
//
//    @Override
//    public void doStop() throws Exception {
//        client.stop();
//        super.doStop();
//    }

    public void populateExchangeFromRestletResponse(Exchange exchange, Response response) throws Exception {


        // set response code
        int responseCode = response.getStatus();
//        Response response2 = response;
//        Object o = response.readEntity(Object.class);
        System.out.println("ojekt: ");
        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, responseCode);

        // set restlet response as header so end user have access to it if needed
        exchange.getOut().setHeader("RESTEASY_RESPONSE", response);
//        exchange.getOut().setHeader("RESTEASY_Entity", o);
        //TODO: not working
//        exchange.getOut().setBody(response.readEntity(Object.class));

        exchange.getOut().setBody(response.readEntity(String.class));
        // preserve headers from in by copying any non existing headers
        // to avoid overriding existing headers with old values
        MessageHelper.copyHeaders(exchange.getIn(), exchange.getOut(), false);
    }

}
