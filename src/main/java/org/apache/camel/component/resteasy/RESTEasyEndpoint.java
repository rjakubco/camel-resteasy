package org.apache.camel.component.resteasy;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.component.http.HttpEndpoint;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.spi.HeaderFilterStrategyAware;
import org.apache.camel.spi.UriParam;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class RESTEasyEndpoint extends HttpEndpoint implements HeaderFilterStrategyAware {
    // TODO poupratovat a vymazat nepotrebne
//    private static final int DEFAULT_PORT = 80;
//    private static final String DEFAULT_PROTOCOL = "http";
//    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_SOCKET_TIMEOUT = 30000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 30000;
    private  String resteasyMethod = "GET";

    private HttpServletDispatcher dispatcher;

    @UriParam
    private String servletName;

    @UriParam
    private Boolean proxy = false;

    private String protocol;
    private String host;
    private int port ;
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private String uriPattern;
    private RESTEasyConfiguration configuration;

    private HeaderFilterStrategy headerFilterStrategy;

    private boolean throwExceptionOnFailure = true;
    private boolean disableStreamCache;



    public RESTEasyEndpoint(String endPointURI, RESTEasyComponent component, URI httpUri, HttpClientParams params, HttpConnectionManager httpConnectionManager,
                            HttpClientConfigurer clientConfigurer) throws URISyntaxException {
        super(endPointURI, component, httpUri, params, httpConnectionManager, clientConfigurer);
    }

    public Boolean getProxy() {
        return proxy;
    }

    public void setProxy(Boolean proxy) {
        this.proxy = proxy;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public String getResteasyMethod() {
        return resteasyMethod;
    }

    public void setResteasyMethod(String resteasyMethod) {
        this.resteasyMethod = resteasyMethod;
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
        RESTEasyConsumer answer = new RESTEasyConsumer(this, processor);
        configureConsumer(answer);
        return answer;
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
