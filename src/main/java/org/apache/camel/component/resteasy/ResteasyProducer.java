package org.apache.camel.component.resteasy;

import org.apache.camel.CamelExchangeException;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class ResteasyProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(ResteasyProducer.class);
    ResteasyEndpoint endpoint;
    private static final Pattern PATTERN = Pattern.compile("\\(([\\w\\.]*)\\)");


    public ResteasyProducer(ResteasyEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }


    @Override
    public void process(Exchange exchange) throws Exception {
        ResteasyEndpoint endpoint = (ResteasyEndpoint) getEndpoint();

        LOG.info("Uri pattern from endpoint: " + endpoint.getUriPattern());
        String resourceUri = buildUri(endpoint, exchange);
        LOG.info("Final URI: " + resourceUri);

        Map<String, String> parameters = getParamaters(exchange, endpoint);

        if(endpoint.getProxyClientClass() != null){
            // Proxy producer
            if(endpoint.getProxyClientClass().isEmpty()){
                throw new IllegalArgumentException("Uri option proxyClientClass cannot be empty! Full class name must be specified.");
            } else{
                endpoint.getRestEasyHttpBinding().populateProxyResteasyRequestAndExecute(resourceUri, exchange, parameters);
            }
        } else{
            // Basic producer
            Response response = endpoint.getRestEasyHttpBinding().populateResteasyRequestFromExchangeAndExecute(resourceUri, exchange, parameters);
            endpoint.getRestEasyHttpBinding().populateExchangeFromResteasyResponse(exchange, response);

            response.close();
        }

    }


    private static String buildUri(ResteasyEndpoint endpoint, Exchange exchange) throws CamelExchangeException {
        String uri;
        if(endpoint.getPort() == 0){
            uri = endpoint.getProtocol() + "://" + endpoint.getHost()  + endpoint.getUriPattern();
        } else{
            uri = endpoint.getProtocol() + "://" + endpoint.getHost() + ":" + endpoint.getPort() + endpoint.getUriPattern();
        }


        // substitute { } placeholders in uri and use mandatory headers
        LOG.trace("Substituting '(value)' placeholders in uri: {}", uri);
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
            LOG.debug("Adding query: {} to uri: {}", query, uri);
            uri = addQueryToUri(uri, query);
        }

        LOG.debug("Using uri: {}", uri);
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

    protected static Map<String, String> getParamaters(Exchange exchange, ResteasyEndpoint endpoint){
        Map<String, String> parameters = new HashMap<String, String>();

        // Get method which should be used on producer
        String method = endpoint.getResteasyMethod();
        String methodHeader = exchange.getIn().getHeader(ResteasyConstants.RESTEASY_HTTP_METHOD, String.class);

        if(methodHeader != null && !method.equalsIgnoreCase(methodHeader)){
            method = methodHeader;
        }

        parameters.put("method", method);

        // Get parameters for proxy producer
        String proxyClassName = endpoint.getProxyClientClass();
        parameters.put("proxyClassName", proxyClassName);

        String proxyMethodName = endpoint.getProxyMethod();
        String proxyMethodNameHeader = exchange.getIn().getHeader(ResteasyConstants.RESTEASY_PROXY_METHOD, String.class);
        if(proxyMethodNameHeader != null && !proxyMethodName.equalsIgnoreCase(proxyMethodNameHeader) ){
            proxyMethodName = proxyMethodNameHeader;
        }
        parameters.put("proxyMethodName", proxyMethodName);

        // Get parameters for basic authentication
        String usernameHeader = exchange.getIn().getHeader(ResteasyConstants.RESTEASY_USERNAME, String.class);
        String passwordHeader = exchange.getIn().getHeader(ResteasyConstants.RESTEASY_PASSWORD, String.class);
        String username = endpoint.getUsername();
        String password = endpoint.getPassword();

        if(usernameHeader != null && !username.equals(usernameHeader)){
            username = usernameHeader;
        }
        if(passwordHeader != null && !password.equals(passwordHeader)){
            password = passwordHeader;
        }
        parameters.put("username", username);
        parameters.put("password", password);

        return parameters;
    }

}
