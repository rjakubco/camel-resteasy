package org.apache.camel.component.resteasy;

import org.apache.camel.Exchange;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.util.MessageHelper;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jboss.resteasy.client.jaxrs.BasicAuthentication;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The default Resteasy binding implementation
 *
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public class DefaultResteasyHttpBinding implements ResteasyHttpBinding {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultResteasyHttpBinding.class);

    private HeaderFilterStrategy headerFilterStrategy;

    public HeaderFilterStrategy getHeaderFilterStrategy() {
        return headerFilterStrategy;
    }

    @Override
    public void setHeaderFilterStrategy(HeaderFilterStrategy headerFilterStrategy) {
        this.headerFilterStrategy = headerFilterStrategy;
    }

    @Override
    public Response populateResteasyRequestFromExchangeAndExecute(String uri, Exchange exchange, Map<String, String> parameters) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        String body = exchange.getIn().getBody(String.class);

        LOG.debug("Body in producer: " + body);

        String mediaType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE, String.class);

        ResteasyWebTarget target = client.target(uri);

        LOG.debug("Populate Resteasy request from exchange body: {} using media type {}", body, mediaType);

        Invocation.Builder builder;
        if(mediaType != null){
            builder = target.request(mediaType);
        } else{
            builder = target.request();
        }


        for (Map.Entry<String, Object> entry : exchange.getIn().getHeaders().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (headerFilterStrategy != null
                    && !headerFilterStrategy.applyFilterToCamelHeaders(key, value, exchange)) {
                    builder.header(key, value);
                LOG.debug("Populate Resteasy request from exchange header: {} value: {}", key, value);
            }
        }

        if(parameters.get("username") != null && parameters.get("password") != null){
            target.register(new BasicAuthentication(parameters.get("username"), parameters.get("password")));
        }
        LOG.debug("Basic authentication was applied");
        String method = parameters.get("method");

        if(method.equals("GET")){
                return builder.get();
        }
        if(method.equals("POST")){
            return  builder.post(Entity.entity(body, mediaType));
        }
        if(method.equals("PUT")){
            return  builder.put(Entity.entity(body, mediaType));
        }
        if(method.equals("DELETE")){
            return  builder.delete();
        }
        if(method.equals("OPTIONS")){
            return  builder.options();
        }
        if(method.equals("TRACE")){
            return  builder.trace();
        }
        if(method.equals("HEAD")){
            return  builder.head();
        }

        // maybe throw exception because not method was correct
        throw new IllegalArgumentException("Method '" + method +"' is not supported method");
    }


    @Override
    public void populateProxyResteasyRequestAndExecute(String uri, Exchange exchange, Map<String, String> parameters){
        ResteasyClient client = new ResteasyClientBuilder().build();

        ResteasyWebTarget target = client.target(uri);

        if(parameters.get("username") != null && parameters.get("password") != null){
            target.register(new BasicAuthentication(parameters.get("username"), parameters.get("password")));
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Basic authentication was applied");
        }

        Class realClazz;
        Object object = null;
        try {
            realClazz = Class.forName(parameters.get("proxyClassName"));
            Object simple = target.proxy(realClazz);
            
            ArrayList headerParams = exchange.getIn().getHeader(ResteasyConstants.RESTEASY_PROXY_METHOD_PARAMS, ArrayList.class);

            if(headerParams != null){
                Object[] args = new Object[headerParams.size()];
                Class[] paramsClasses = new Class[headerParams.size()];
                for(int i = 0; i < headerParams.size(); i++){
                    LOG.debug(headerParams.get(i).getClass().toString());
                    paramsClasses[i] = headerParams.get(i).getClass();
                    args[i] = headerParams.get(i);
                }

                Method m = simple.getClass().getMethod(parameters.get("proxyMethodName"), paramsClasses);
                object = m.invoke(simple, args);
            } else{
                Method m = simple.getClass().getMethod(parameters.get("proxyMethodName"), new Class[] {});
                object = m.invoke(simple, new Object[] {});
            }

            if(object instanceof Response){
                // using proxy client with return type response, creates some problem with readEntity and response needs to be
                // closed manually for correct return type to user.
                populateExchangeFromResteasyResponse(exchange, (Response) object);
                ((Response) object).close();
            } else {
                exchange.getOut().setBody(object);
                // preserve headers from in by copying any non existing headers
                // to avoid overriding existing headers with old values
                MessageHelper.copyHeaders(exchange.getIn(), exchange.getOut(), false);
            }


        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            exchange.getOut().getHeaders().put(ResteasyConstants.RESTEASY_PROXY_PRODUCER_EXCEPTION, ExceptionUtils.getStackTrace(e));
            exchange.getOut().setBody(e);
            LOG.error("Camel RESTEasy proxy exception", e);
        }
    }

    @Override
    public void populateExchangeFromResteasyResponse(Exchange exchange, Response response){
        // set response code
        int responseCode = response.getStatus();
        Map<String, Object> headers = new HashMap<>();
        headers.put(Exchange.HTTP_RESPONSE_CODE, responseCode);

        for (String key : response.getHeaders().keySet()) {
            Object value = response.getHeaders().get(key);
            if (headerFilterStrategy != null
                    && !headerFilterStrategy.applyFilterToExternalHeaders(key, value, exchange)) {
                headers.put(key,value);
                LOG.debug("Populate Camel exchange from response: {} value: {}", key, value);
            }
        }

        // set resteasy response as header so the end user has access to it if needed
        headers.put(ResteasyConstants.RESTEASY_RESPONSE, response);
        exchange.getOut().setHeaders(headers);

        LOG.debug("Headers from exchange.getIn() : " + exchange.getIn().getHeaders().toString());
        LOG.debug("Headers from exchange.getOut() before copying : " + exchange.getOut().getHeaders().toString());
        LOG.debug("Header from response : " + response.getHeaders().toString());

        if(response.hasEntity()){
            exchange.getOut().setBody(response.readEntity(String.class));
        } else{
            exchange.getOut().setBody(response.getStatusInfo());
        }

        // preserve headers from in by copying any non existing headers
        // to avoid overriding existing headers with old values
        MessageHelper.copyHeaders(exchange.getIn(), exchange.getOut(), false);
    }


}
