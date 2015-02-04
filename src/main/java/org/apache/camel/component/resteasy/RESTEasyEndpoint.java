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
    private  String resteasyMethod = "GET";

    private HttpServletDispatcher dispatcher;

    @UriParam
    private String servletName;

    @UriParam
    private String proxyClientClass;

    @UriParam
    private String proxyMethod;

    @UriParam
    private Boolean proxy = false;

    @UriParam
    private Boolean OauthSecure;


    private String protocol;
    private String host;
    private int port;
    private String uriPattern;
    private RESTEasyConfiguration configuration;

    private HeaderFilterStrategy headerFilterStrategy;

    @UriParam
    private boolean throwExceptionOnFailure = true;
    private boolean disableStreamCache;


    public String getProxyMethod() {
        return proxyMethod;
    }

    public void setProxyMethod(String proxyMethod) {
        this.proxyMethod = proxyMethod;
    }

    public Boolean getProxy() {
        return proxy;
    }

    public void setProxy(Boolean proxy) {
        this.proxy = proxy;
    }

    public RESTEasyEndpoint(String endPointURI, RESTEasyComponent component, URI httpUri, HttpClientParams params, HttpConnectionManager httpConnectionManager,
                            HttpClientConfigurer clientConfigurer) throws URISyntaxException {
        super(endPointURI, component, httpUri, params, httpConnectionManager, clientConfigurer);

    }

    public String getProxyClientClass() {
        return proxyClientClass;
    }

    public void setProxyClientClass(String proxyClientClass) {
        this.proxyClientClass = proxyClientClass;
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


    public Boolean getOauthSecure() {
        return OauthSecure;
    }

    public void setOauthSecure(Boolean oauthSecure) {
        OauthSecure = oauthSecure;
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
