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


        if(parameters.get("username") != null && parameters.get("password") != null){
            target.register(new BasicAuthentication(parameters.get("username"), parameters.get("password")));
        }

        LOG.debug("Basic authentication was applied");

        if(parameters.get("method").equals("GET")){
            if(mediaType == null){

                return target.request().get();
            } else {
                return target.request(mediaType).get();
            }
        }
        if(parameters.get("method").equals("POST")){
            return  target.request(mediaType).post(Entity.entity(body, mediaType));

        }
        if(parameters.get("method").equals("PUT")){
            return  target.request(mediaType).put(Entity.entity(body, mediaType));
        }
        if(parameters.get("method").equals("DELETE")){
            return  target.request(mediaType).delete();
        }




////
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
////
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


    @Override
    public void populateProxyResteasyRequestAndExecute(String uri, Exchange exchange, Map<String, String> parameters){
        ResteasyClient client = new ResteasyClientBuilder().build();

        ResteasyWebTarget target = client.target(uri);


        if(parameters.get("username") != null && parameters.get("password") != null){
            target.register(new BasicAuthentication(parameters.get("username"), parameters.get("password")));
        }

        LOG.info(exchange.getIn().getHeaders().toString());
        LOG.info(parameters.toString());
        LOG.debug("Basic authentication was applied");

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
