package org.apache.camel.component.resteasy;

import org.apache.camel.CamelContext;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Processor;
import org.apache.camel.component.http.AuthMethod;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.spi.RestConfiguration;
import org.apache.camel.spi.RestConsumerFactory;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.URISupport;
import org.apache.camel.util.UnsafeUriCharactersEncoder;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

import java.net.URI;
import java.util.*;

/**
 * Defines a Resteasy component, which is extending HttpComponent
 *
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public class ResteasyComponent extends HttpComponent implements RestConsumerFactory {

    private HttpRegistry httpRegistry;

    // parameter that can be set in ResteasyComponent
    private String proxyConsumersClasses;

//    private String servletName;

    private ResteasyHttpBinding resteasyHttpBinding;

    public ResteasyComponent(){
        super(ResteasyEndpoint.class);
        this.resteasyHttpBinding = new DefaultResteasyHttpBinding();
    }

    public ResteasyComponent(HttpServletDispatcher dispatcher){
        super(ResteasyEndpoint.class);

    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        HttpClientParams params = new HttpClientParams();

        // create the configurer to use for this endpoint
        final Set<AuthMethod> authMethods = new LinkedHashSet<AuthMethod>();
        HttpClientConfigurer configurer = createHttpClientConfigurer(parameters, authMethods);

        // must extract well known parameters before we create the endpoint
        Boolean throwExceptionOnFailure = getAndRemoveParameter(parameters, "throwExceptionOnFailure", Boolean.class);
        Boolean transferException = getAndRemoveParameter(parameters, "transferException", Boolean.class);
        Boolean matchOnUriPrefix = getAndRemoveParameter(parameters, "matchOnUriPrefix", Boolean.class);
//        String servletName = getAndRemoveParameter(parameters, "servletName", String.class, getServletName());
        String httpMethodRestrict = getAndRemoveParameter(parameters, "httpMethodRestrict", String.class);
        HeaderFilterStrategy headerFilterStrategy = resolveAndRemoveReferenceParameter(parameters, "headerFilterStrategy", HeaderFilterStrategy.class);

        // restructure uri to be based on the parameters left as we don't want to include the Camel internal options
        URI httpUri = URISupport.createRemainingURI(new URI(UnsafeUriCharactersEncoder.encodeHttpURI(uri)), parameters);

        ResteasyEndpoint endpoint =  new ResteasyEndpoint(uri, this, httpUri, params, getHttpConnectionManager(), configurer);

//        endpoint.setServletName(servletName);
        // Needed for taking component options from URI and using only clean uri for resource. Later adding query parameters
        setProperties(endpoint, parameters);

        if (endpoint.getRestEasyHttpBinding() == null) {
            endpoint.setRestEasyHttpBinding(resteasyHttpBinding);
        }

        if (matchOnUriPrefix != null) {
            endpoint.setMatchOnUriPrefix(matchOnUriPrefix);
        }
        //set parameters if they exists in URI
        if (httpMethodRestrict != null) {
            endpoint.setHttpMethodRestrict(httpMethodRestrict);
        }
        if (headerFilterStrategy != null) {
            endpoint.setHeaderFilterStrategy(headerFilterStrategy);
        }
        if (throwExceptionOnFailure != null) {
            endpoint.setThrowExceptionOnFailure(throwExceptionOnFailure);
        }
        if (transferException != null) {
            endpoint.setTransferException(transferException);
        }

        // construct URI so we can use it to get the splitted information
        URI u = new URI(remaining);
        String protocol = u.getScheme();

        String uriPattern = u.getPath();
        if (parameters.size() > 0) {
            uriPattern = uriPattern + "?" + URISupport.createQueryString(parameters);
        }

        int port = 0;
        String host = u.getHost();
        if (u.getPort() > 0) {
            port = u.getPort();
        }

        endpoint.setProtocol(protocol);
        endpoint.setUriPattern(uriPattern);
        endpoint.setHost(host);
        if (port > 0) {
            endpoint.setPort(port);
        }
        return endpoint;
    }

    @Override
    public Consumer createConsumer(CamelContext camelContext, Processor processor, String verb, String basePath, String uriTemplate, String consumes, String produces, Map<String, Object> parameters) throws Exception {

        String path = basePath;
        if (uriTemplate != null) {
            // make sure to avoid double slashes
            if (uriTemplate.startsWith("/")) {
                path = path + uriTemplate;
            } else {
                path = path + "/" + uriTemplate;
            }
        }
        path = FileUtil.stripLeadingSeparator(path);

        // if no explicit port/host configured, then use port from rest configuration
        RestConfiguration config = getCamelContext().getRestConfiguration();

        // TODO check possibility with rest dsl -> in this section some additional code will be
        Map<String, Object> map = new HashMap<String, Object>();
        // build query string, and append any endpoint configuration properties
        if (config.getComponent() == null || config.getComponent().equals("resteasy")) {
            // setup endpoint options
            if (config.getEndpointProperties() != null && !config.getEndpointProperties().isEmpty()) {
                map.putAll(config.getEndpointProperties());
            }
        }

        String query = URISupport.createQueryString(map);

        String url = "resteasy:/%s";
        if (!query.isEmpty()) {
            url = url + "?" + query;
        }

        // must use upper case for restrict
        String restrict = verb.toUpperCase(Locale.US);

        // get the endpoint
        url = String.format(url, path, restrict);

        ResteasyEndpoint endpoint = camelContext.getEndpoint(url,ResteasyEndpoint.class);
        setProperties(endpoint, parameters);

        return endpoint.createConsumer(processor);
    }

    @Override
    public void connect(HttpConsumer consumer) throws Exception {
        ResteasyConsumer sc = (ResteasyConsumer) consumer;
        String name = sc.getEndpoint().getServletName();
        HttpRegistry registry = httpRegistry;
        if (registry == null) {
            registry = DefaultHttpRegistry.getHttpRegistry(name);
        }
        registry.register(consumer);
    }

    @Override
    public void disconnect(HttpConsumer consumer) throws Exception {
        ResteasyConsumer sc = (ResteasyConsumer) consumer;
        String name = sc.getEndpoint().getServletName();
        HttpRegistry registry = httpRegistry;
        if (registry == null) {
            registry = DefaultHttpRegistry.getHttpRegistry(name);
        }
        registry.unregister(consumer);
    }

    public String getProxyConsumersClasses() {
        return proxyConsumersClasses;
    }

    public void setProxyConsumersClasses(String proxyConsumersClasses) {
        this.proxyConsumersClasses = proxyConsumersClasses;
    }

//    public String getServletName() {
//        return servletName;
//    }
//
//    public void setServletName(String servletName) {
//        this.servletName = servletName;
//    }

    public HttpRegistry getHttpRegistry() {
        return httpRegistry;
    }

    public void setHttpRegistry(HttpRegistry httpRegistry) {
        this.httpRegistry = httpRegistry;
    }

}
