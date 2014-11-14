package org.apache.camel.component.resteasy.servlet;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.http.HttpMessage;
import org.apache.camel.component.http.HttpServletResolveConsumerStrategy;
import org.apache.camel.component.http.ServletResolveConsumerStrategy;
import org.apache.camel.component.http.helper.HttpHelper;
import org.apache.camel.component.resteasy.DefaultHttpRegistry;
import org.apache.camel.component.resteasy.HttpRegistry;
import org.apache.camel.component.resteasy.RESTEasyEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by roman on 18/10/14.
 */
public class RESTEasyCamelServlet extends HttpServletDispatcher {
    private HttpRegistry httpRegistry;

    private String servletName;

    private ServletResolveConsumerStrategy servletResolveConsumerStrategy = new HttpServletResolveConsumerStrategy();
    private final ConcurrentMap<String, HttpConsumer> consumers = new ConcurrentHashMap<String, HttpConsumer>();

    private static final Logger LOG = LoggerFactory.getLogger(RESTEasyCamelServlet.class);



    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        // basic finding all interface with annotation Path -> TODO: check maybe even for application
        Reflections reflections = new Reflections("");
        Set<Class<?>> annotated =
                reflections.getTypesAnnotatedWith(Path.class);

        System.out.println(annotated);
        // Iterate over all found classes and if found interfaces have implementation on classpath
        for(Class<?> c : annotated){
            if(c.isInterface()){
                try {
                    Class clazz = Class.forName(c.getName());
                    Set subTypes = reflections.getSubTypesOf(clazz);
                    if(subTypes.isEmpty()){
                        // Create dynamic proxy class implementing interface
                        InvocationHandler handler = new RESTEasyInvocationHandler();
                        Object  proxy = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, handler);

                        // register new created proxy to the resteasy registry
                        getDispatcher().getRegistry().addSingletonResource(proxy);
                        System.out.println(getDispatcher().getRegistry().toString());
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        String name = servletConfig.getServletName();
        if (httpRegistry == null) {
            httpRegistry = DefaultHttpRegistry.getHttpRegistry(name);
            RESTEasyCamelServlet existing = httpRegistry.getCamelServlet(name);
            if (existing != null) {
                String msg = "Duplicate ServletName detected: " + name + ". Existing: " + existing + " This: " + this.toString()
                        + ". Its advised to use unique ServletName per Camel application.";
                // always log so people can see it easier
                LOG.info(msg);
            }
            httpRegistry.register(this);
        }

    }

    private RESTEasyEndpoint getServletEndpoint(HttpConsumer consumer) {
        if (!(consumer.getEndpoint() instanceof RESTEasyEndpoint)) {
            throw new RuntimeException("Invalid consumer type. Must be ServletEndpoint but is "
                    + consumer.getClass().getName());
        }
        return (RESTEasyEndpoint)consumer.getEndpoint();
    }

    @Override
    protected void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {

        // From camel servlet
        LOG.trace("Service: {}", httpServletRequest);

        // Is there a consumer registered for the request.
        HttpConsumer consumer = resolve(httpServletRequest);

        if (consumer == null) {
            LOG.info("No consumer to service request {}", httpServletRequest);
            // No consumer found in routes let resteasy dispatcher process the request -> returning unchanged rest answer
            super.service(httpServletRequest, httpServletResponse);
            return;
        }
        // are we suspended?
        if (consumer.isSuspended()) {
            LOG.info("Consumer suspended, cannot service request {}", httpServletRequest);
            httpServletResponse.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            super.service(httpServletRequest, httpServletResponse);
            return;
        }

        // if its an OPTIONS request then return which method is allowed
        if ("OPTIONS".equals(httpServletRequest.getMethod())) {
            String s;
            if (consumer.getEndpoint().getHttpMethodRestrict() != null) {
                s = "OPTIONS," + consumer.getEndpoint().getHttpMethodRestrict();
            } else {
                // allow them all
                s = "GET,HEAD,POST,PUT,DELETE,TRACE,OPTIONS,CONNECT,PATCH";
            }
            httpServletResponse.addHeader("Allow", s);
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            super.service(httpServletRequest, httpServletResponse);
            return;
        }

        // TODO myslim ze zbytocne a nepouzitelne v mojom
//        if (consumer.getEndpoint().getHttpMethodRestrict() != null
//                && !consumer.getEndpoint().getHttpMethodRestrict().contains(httpServletRequest.getMethod())) {
//            httpServletResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
//            super.service(httpServletRequest, httpServletResponse);
//            return;
//        }

        if ("TRACE".equals(httpServletRequest.getMethod()) && !consumer.isTraceEnabled()) {
            httpServletResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            super.service(httpServletRequest, httpServletResponse);
            return;
        }

        // create exchange and set data on it
        Exchange exchange = new DefaultExchange(consumer.getEndpoint(), ExchangePattern.InOut);

        if (consumer.getEndpoint().isBridgeEndpoint()) {
            exchange.setProperty(Exchange.SKIP_GZIP_ENCODING, Boolean.TRUE);
            exchange.setProperty(Exchange.SKIP_WWW_FORM_URLENCODED, Boolean.TRUE);
        }
        if (consumer.getEndpoint().isDisableStreamCache()) {
            exchange.setProperty(Exchange.DISABLE_HTTP_STREAM_CACHE, Boolean.TRUE);
        }

        // we override the classloader before building the HttpMessage just in case the binding
        // does some class resolution
//        ClassLoader oldTccl = overrideTccl(exchange);

        System.out.println("Zavolany super.service prvy");
        //TODO problem s tym ze ked mam proxy tak to nepojde. Lebo po zavolani tohto AS vyhodnoti ako 404. Ked to nezavolam tak to funguje ale zvysok
        // -> tym padom asi treba spravit premenu ktora bude udavat ci je consumer proxy alebo pocuva na dakej definovanej adrese...

        if( ! getServletEndpoint(consumer).getProxy()){
            // If proxy option is false then there is implementation of RestEasy. If true then skip this because it is only proxy
            super.service(httpServletRequest, httpServletResponse);

            // If request wasn't successful in resteasy then stop processing and return created response from resteasy
            if(httpServletResponse.getStatus() != 200){
                return;
            }


        }

        LOG.info("Copier service: " + new String(((RESTEasyHttpServletResponseWrapper) httpServletResponse).getCopy(), httpServletResponse.getCharacterEncoding()));


        HttpHelper.setCharsetFromContentType(httpServletRequest.getContentType(), exchange);
        HttpMessage m = new HttpMessage(exchange, httpServletRequest, httpServletResponse);

        // TODO na teraz este vracia odpoved zo restu. Ale len ako String mozno to treba rozsirit na nieco lepsie...
        String response = new String(((RESTEasyHttpServletResponseWrapper) httpServletResponse).getCopy(), httpServletResponse.getCharacterEncoding());
        m.setBody(response);
        exchange.setIn(m);

        String contextPath = consumer.getEndpoint().getPath();
        exchange.getIn().setHeader("CamelServletContextPath", contextPath);

        String httpPath = (String)exchange.getIn().getHeader(Exchange.HTTP_PATH);
        // here we just remove the CamelServletContextPath part from the HTTP_PATH
        if (contextPath != null
                && httpPath.startsWith(contextPath)) {
            exchange.getIn().setHeader(Exchange.HTTP_PATH,
                    httpPath.substring(contextPath.length()));
        }

//        // we want to handle the UoW
//        try {
//            consumer.createUoW(exchange);
//        } catch (Exception e) {
//            LOG.error("Error processing request", e);
//            throw new ServletException(e);
//        }

        try {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Processing request for exchangeId: {}", exchange.getExchangeId());
            }
            // process the exchange
            consumer.getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }


        try {
            // now lets output to the response
            if (LOG.isTraceEnabled()) {
                LOG.trace("Writing response for exchangeId: {}", exchange.getExchangeId());
            }
            // pointless
//            Integer bs = consumer.getEndpoint().getResponseBufferSize();
//            if (bs != null) {
//                LOG.info("Using response buffer size: {}", bs);
//                httpServletResponse.setBufferSize(bs);
//            }

            // Reset buffer in response because it was sent to consumer for processing -> route handled response and return what it should
            if( !response.isEmpty() ){
                httpServletResponse.resetBuffer();
            }

            // TODO reset headers too? Or they will be overwritten?

            consumer.getBinding().writeResponse(exchange, httpServletResponse);

        } catch (IOException e) {
            LOG.error("Error processing request", e);
            throw e;
        } catch (Exception e) {
            LOG.error("Error processing request", e);
            throw new ServletException(e);
        }
//        } finally {
//            consumer.doneUoW(exchange);
////            restoreTccl(exchange, oldTccl);
//        }
    }


    public void connect(HttpConsumer consumer) {

        RESTEasyEndpoint endpoint = getServletEndpoint(consumer);
       // TODO nie som isty co to robi tento if. Ale malo to nieco s OSGI spolocne a kvoli tomu neregsitrovalo consumera
        //TODO alebo ked bude viac servletov s inym menom moze by problem. Doriesit v buducnosti ked to bude akutne
//        if (endpoint.getServletName() != null && endpoint.getServletName().equals(getServletName())) {
//            System.out.println("vlozeny consumer");
            consumers.put(consumer.getPath(), consumer);
//        }
    }
//
    public void destroy() {
        DefaultHttpRegistry.removeHttpRegistry(getServletName());
        if (httpRegistry != null) {
            httpRegistry.unregister(this);
            httpRegistry = null;
        }
        LOG.info("Destroyed CamelHttpTransportServlet[{}]", getServletName());
    }

    public void disconnect(HttpConsumer consumer) {
        LOG.info("Disconnecting consumer: {}", consumer);
        consumers.remove(consumer.getPath());
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public ServletResolveConsumerStrategy getServletResolveConsumerStrategy() {
        return servletResolveConsumerStrategy;
    }

    public void setServletResolveConsumerStrategy(ServletResolveConsumerStrategy servletResolveConsumerStrategy) {
        this.servletResolveConsumerStrategy = servletResolveConsumerStrategy;
    }

    public Map<String, HttpConsumer> getConsumers() {
        return Collections.unmodifiableMap(consumers);
    }

    // TODO zatial toto odhaluje consumera. Nie som si isty uplne aku to bude mat rolu. Ale je mozne ze to bude treba vylepsit

    protected HttpConsumer resolve(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path == null) {
            return null;
        }
        HttpConsumer answer = consumers.get(path);

        if (answer == null) {
            for (String key : consumers.keySet()) {
                if (consumers.get(key).getEndpoint().isMatchOnUriPrefix() && path.startsWith(key)) {
                    answer = consumers.get(key);
                    break;
                }
            }
        }
        return answer;





//        return getServletResolveConsumerStrategy().resolve(request, getConsumers());
    }



}

