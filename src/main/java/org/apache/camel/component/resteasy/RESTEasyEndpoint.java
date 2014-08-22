package org.apache.camel.component.resteasy;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.spi.HeaderFilterStrategyAware;


import java.util.List;
import java.util.Map;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RESTEasyEndpoint extends DefaultEndpoint implements HeaderFilterStrategyAware {
//    private static final int DEFAULT_PORT = 80;
//    private static final String DEFAULT_PROTOCOL = "http";
//    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_SOCKET_TIMEOUT = 30000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
//    private Method restletMethod = Method.GET;

    // Optional and for consumer only. This allows a single route to service multiple methods.
    // If it is non-null then restletMethod is ignored.
//    private Method[] restletMethods;

    private String protocol;
    private String host;
    private int port ;
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private String uriPattern;
    private RESTEasyConfiguration configuration;
    private RESTEasyBinding restEasyBinding;

    // Optional and for consumer only. This allows a single route to service multiple URI patterns.
    // The URI pattern defined in the endpoint will still be honored.
//    private List<String> restletUriPatterns;
//
//    private Map<String, String> restletRealm;
    private HeaderFilterStrategy headerFilterStrategy;
//    private RestletBinding restletBinding;
    private boolean throwExceptionOnFailure = true;
    private boolean disableStreamCache;


    public RESTEasyEndpoint(String endpointUri, Component component, RESTEasyConfiguration configuration){
        super(endpointUri,component);
        this.configuration = configuration;
    }

    public static int getDefaultSocketTimeout() {
        return DEFAULT_SOCKET_TIMEOUT;
    }

    public static int getDefaultConnectTimeout() {
        return DEFAULT_CONNECT_TIMEOUT;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getUriPattern() {
        return uriPattern;
    }

    public void setUriPattern(String uriPattern) {
        this.uriPattern = uriPattern;
    }

    public RESTEasyConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RESTEasyConfiguration configuration) {
        this.configuration = configuration;
    }

    public RESTEasyBinding getRestEasyBinding() {
        return restEasyBinding;
    }

    public void setRestEasyBinding(RESTEasyBinding restEasyBinding) {
        this.restEasyBinding = restEasyBinding;
    }

    public boolean isThrowExceptionOnFailure() {
        return throwExceptionOnFailure;
    }

    public void setThrowExceptionOnFailure(boolean throwExceptionOnFailure) {
        this.throwExceptionOnFailure = throwExceptionOnFailure;
    }

    public boolean isDisableStreamCache() {
        return disableStreamCache;
    }

    public void setDisableStreamCache(boolean disableStreamCache) {
        this.disableStreamCache = disableStreamCache;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new RESTEasyProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new RESTEasyConsumer(this, processor);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public HeaderFilterStrategy getHeaderFilterStrategy() {
        return null;
    }

    @Override
    public void setHeaderFilterStrategy(HeaderFilterStrategy strategy) {

    }


}
