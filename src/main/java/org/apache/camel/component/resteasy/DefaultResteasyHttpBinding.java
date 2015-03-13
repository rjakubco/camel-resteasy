package org.apache.camel.component.resteasy;

import org.apache.camel.Exchange;
import org.apache.camel.component.http.HttpHeaderFilterStrategy;
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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class DefaultResteasyHttpBinding implements ResteasyHttpBinding {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultResteasyHttpBinding.class);

    //use default filter strategy from Camel HTTP
    private HeaderFilterStrategy headerFilterStrategy = new HttpHeaderFilterStrategy();

    @Override
    public Response populateResteasyRequestFromExchangeAndExecute(String uri, Exchange exchange, Map<String, String> parameters) {
        ResteasyClient client = new ResteasyClientBuilder().build();

        String body = exchange.getIn().getBody(String.class);

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

        if(parameters.get("method").equals("GET")){
                return builder.get();
        }
        if(parameters.get("method").equals("POST")){
            return  builder.post(Entity.entity(body, mediaType));

        }
        if(parameters.get("method").equals("PUT")){

            return  builder.put(Entity.entity(body, mediaType));
        }
        if(parameters.get("method").equals("DELETE")){
            return  builder.delete();
        }
        // TODO: add all methods becasue these 4 are not all


        // maybe throw exception because not method was correct
        throw new IllegalArgumentException("Method for Resteasy client was not from list of supported methods");
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
                Class[] paramsClasses = new Class[headerParams.size()];
                for(int i = 0; i < headerParams.size(); i++){
                    LOG.trace(headerParams.get(i).getClass().toString());
                    paramsClasses[i] = headerParams.get(i).getClass();
                }

                Method m = simple.getClass().getMethod(parameters.get("proxyMethodName"), paramsClasses);
                object = m.invoke(simple, "test");
            } else{
                Method m = simple.getClass().getMethod(parameters.get("proxyMethodName"), new Class[] {});
                object = m.invoke(simple, new Object[] {});
            }

            if(object instanceof Response){
                // using proxy client with return type response, creates some problem with readEntity and response needs to be
                // closed manually for correct return type to user.
                exchange.getOut().setBody(((Response)object).readEntity(String.class));
                ((Response) object).close();
            } else {
                exchange.getOut().setBody(object);
            }
            // preserve headers from in by copying any non existing headers
            // to avoid overriding existing headers with old values
            MessageHelper.copyHeaders(exchange.getIn(), exchange.getOut(), false);
            // TODO change exception handling
        } catch (ClassNotFoundException e) {
            exchange.getOut().setBody(ExceptionUtils.getStackTrace(e));
            LOG.error("Camel RESTEasy proxy exception", e);
        } catch (InvocationTargetException e) {
            exchange.getOut().setBody(ExceptionUtils.getStackTrace(e));
            LOG.error("Camel RESTEasy proxy exception", e);
        } catch (NoSuchMethodException e) {
            exchange.getOut().setBody(ExceptionUtils.getStackTrace(e));
            LOG.error("Camel RESTEasy proxy exception", e);
        } catch (IllegalAccessException e) {
            exchange.getOut().setBody(ExceptionUtils.getStackTrace(e));
            LOG.error("Camel RESTEasy proxy exception", e);
        }
    }

    @Override
    public void populateExchangeFromResteasyResponse(Exchange exchange, Response response){
        // set response code
        int responseCode = response.getStatus();

        exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, responseCode);

        // set resteasy response as header so end user have access to it if needed
        exchange.getOut().setHeader(ResteasyConstants.RESTEASY_RESPONSE, response);

        //TODO not sure if this is satisfactory, only converting the Entity to String.
        exchange.getOut().setBody(response.readEntity(String.class));

        // preserve headers from in by copying any non existing headers
        // to avoid overriding existing headers with old values
        MessageHelper.copyHeaders(exchange.getIn(), exchange.getOut(), false);
    }


}
