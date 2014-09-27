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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RESTEasyProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(RESTEasyProducer.class);
    private ResteasyClient client;
    private static final Pattern PATTERN = Pattern.compile("\\(([\\w\\.]*)\\)");


    public RESTEasyProducer(Endpoint endpoint) {
        super(endpoint);
        client = new ResteasyClientBuilder().build();

    }


    @Override
    public void process(Exchange exchange) throws Exception {
        RESTEasyEndpoint endpoint = (RESTEasyEndpoint) getEndpoint();

        LOG.trace("Uri pattern from endpoint: " + endpoint.getUriPattern());
        String resourceUri = buildUri(endpoint, exchange);
        LOG.trace("Final URI: " + resourceUri);
        Response response = populateResteasyRequestFromExchangeAndExecute(resourceUri, exchange);
        populateExchangeFromResteasyResponse(exchange, response);

        response.close();
    }



    public void populateExchangeFromResteasyResponse(Exchange exchange, Response response) throws Exception {
        // set response code
        int responseCode = response.getStatus();

        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, responseCode);

        // set resteasy response as header so end user have access to it if needed
        exchange.getOut().setHeader("RESTEASY_RESPONSE", response);

        //TODO: not working, it can be only applied to String from input stream?
//        exchange.getOut().setBody(response.readEntity(Object.class));
        exchange.getOut().setBody(response.readEntity(String.class));

        // preserve headers from in by copying any non existing headers
        // to avoid overriding existing headers with old values
        MessageHelper.copyHeaders(exchange.getIn(), exchange.getOut(), false);
    }

    public Response populateResteasyRequestFromExchangeAndExecute(String uri, Exchange exchange) {
        RESTEasyEndpoint endpoint = (RESTEasyEndpoint) getEndpoint();
        String method = endpoint.getResteasyMethod();
        String methodHeader = exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class);
        //TODO HTTP method in header has priority? maybe change in the future .. ask Jirka
        if(methodHeader != null && !method.equalsIgnoreCase(methodHeader)){
            method = methodHeader;
        }

        String body = exchange.getIn().getBody(String.class);
//        Form form = new Form();
//        // add the body as the key in the form with null value
//        form.add(body, null);

        MediaType mediaType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, MediaType.class);


        ResteasyWebTarget target = client.target(uri);
        //TODO: treba dokonct a vyskusat.
        LOG.debug("Populate Resteasy request from exchange body: {} using media type {}", body, mediaType);
        if(method.equals("GET")){
            if(mediaType == null){
                return target.request().get();
            } else {
                return target.request(mediaType).get();
            }
        }
        if(method.equals("POST")){
            return  target.request(mediaType).post(Entity.entity(body, mediaType));
        }
        if(method.equals("PUT")){
            return  target.request(mediaType).put(Entity.entity(body, mediaType));
        }
        if(method.equals("DELETE")){
            return  target.request(mediaType).delete();
        }

        // login and password are filtered by header filter strategy
//        String login = exchange.getIn().getHeader(RestletConstants.RESTLET_LOGIN, String.class);
//        String password = exchange.getIn().getHeader(RestletConstants.RESTLET_PASSWORD, String.class);
//
//        if (login != null && password != null) {
//            ChallengeResponse authentication = new ChallengeResponse(ChallengeScheme.HTTP_BASIC, login, password);
//            request.setChallengeResponse(authentication);
//            LOG.debug("Basic HTTP Authentication has been applied");
//        }

//        for (Map.Entry<String, Object> entry : exchange.getIn().getHeaders().entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//            if (!headerFilterStrategy.applyFilterToCamelHeaders(key, value, exchange)) {
//                // Use forms only for GET and POST/x-www-form-urlencoded
//                if (request.getMethod() == Method.GET || (request.getMethod() == Method.POST && mediaType == MediaType.APPLICATION_WWW_FORM)) {
//                    if (key.startsWith("org.restlet.")) {
//                        // put the org.restlet headers in attributes
//                        request.getAttributes().put(key, value);
//                    } else {
//                        // put the user stuff in the form
//                        form.add(key, value.toString());
//                    }
//                } else {
//                    // For non-form post put all the headers in attributes
//                    request.getAttributes().put(key, value);
//                }
//                LOG.debug("Populate Restlet request from exchange header: {} value: {}", key, value);
//            }
//        }
//
//        LOG.debug("Using Content Type: {} for POST data: {}", mediaType, body);
//
//        // Only URL Encode for GET and form POST
//        if (request.getMethod() == Method.GET || (request.getMethod() == Method.POST && mediaType == MediaType.APPLICATION_WWW_FORM)) {
//            request.setEntity(form.getWebRepresentation());
//        } else {
//            request.setEntity(body, mediaType);
//        }
//
//        MediaType acceptedMediaType = exchange.getIn().getHeader(Exchange.ACCEPT_CONTENT_TYPE, MediaType.class);
//        if (acceptedMediaType != null) {
//            request.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(acceptedMediaType));
//        }
        return  null;
    }

    private static String buildUri(RESTEasyEndpoint endpoint, Exchange exchange) throws CamelExchangeException {
        String uri = endpoint.getProtocol() + "://" + endpoint.getHost() + ":" + endpoint.getPort() + endpoint.getUriPattern();

        // substitute { } placeholders in uri and use mandatory headers
//        LOG.trace("Substituting '(value)' placeholders in uri: {}", uri);
        Matcher matcher = PATTERN.matcher(uri);
        while (matcher.find()) {
            String key = matcher.group(1);
            String header = exchange.getIn().getHeader(key, String.class);
            // header should be mandatory
            if (header == null) {
                throw new CamelExchangeException("Header with key: " + key + " not found in Exchange", exchange);
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace("Replacing: {} with header value: {}", matcher.group(0), header);
            }

            uri = matcher.replaceFirst(header);
            // we replaced uri so reset and go again
            matcher.reset(uri);
        }

        String query = exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class);
        if (query != null) {
            LOG.trace("Adding query: {} to uri: {}", query, uri);
            uri = addQueryToUri(uri, query);
        }

        LOG.trace("Using uri: {}", uri);
        return uri;
    }

    protected static String addQueryToUri(String uri, String query) {
        if (uri == null || uri.length() == 0) {
            return uri;
        }

        StringBuilder answer = new StringBuilder();

        int index = uri.indexOf('?');
        if (index < 0) {
            answer.append(uri);
            answer.append("?");
            answer.append(query);
        } else {
            answer.append(uri.substring(0, index));
            answer.append("?");
            answer.append(query);
            String remaining = uri.substring(index + 1);
            if (remaining.length() > 0) {
                answer.append("&");
                answer.append(remaining);
            }
        }
        return answer.toString();

    }

}
