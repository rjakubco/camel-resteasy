package org.apache.camel.component.resteasy;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.http.HttpClientConfigurer;
import org.apache.camel.component.http.HttpEndpoint;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.spi.HeaderFilterStrategyAware;
import org.apache.camel.spi.UriParam;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Defines the Resteasy Endpoint.
 * It contains a list of properties for Resteasy endpoint including {@link org.apache.camel.component.resteasy.ResteasyHttpBinding},
 * and {@link HeaderFilterStrategy}.
 *
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public class ResteasyEndpoint extends HttpEndpoint implements HeaderFilterStrategyAware {
    @UriParam
    private  String resteasyMethod = "GET";

    @UriParam
    private ResteasyHttpBinding restEasyHttpBinding;

    @UriParam
    private String servletName;

    @UriParam
    private String proxyClientClass;

    @UriParam
    private String proxyMethod;

    @UriParam
    private Boolean proxy = false;

    @UriParam
    private Boolean camelProxy = false;

    @UriParam
    private String username;

    @UriParam
    private String password;

    private String protocol;
    private String host;
    private int port;
    private String uriPattern;

//    Using default camel headerFilterStrategy -> possibility to create your own strategy and set it on endpoint
    @UriParam
    private HeaderFilterStrategy headerFilterStrategy = new ResteasyHeaderFilterStrategy();

    @UriParam
    private boolean throwExceptionOnFailure = true;


    public ResteasyEndpoint(String endPointURI, ResteasyComponent component, URI httpUri, HttpClientParams params, HttpConnectionManager httpConnectionManager,
                            HttpClientConfigurer clientConfigurer) throws URISyntaxException {
        super(endPointURI, component, httpUri, params, httpConnectionManager, clientConfigurer);

    }

    @Override
    public Producer createProducer() throws Exception {
        return new ResteasyProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        ResteasyConsumer answer = new ResteasyConsumer(this, processor);
        configureConsumer(answer);
        return answer;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public HeaderFilterStrategy getHeaderFilterStrategy() {
        return headerFilterStrategy;
    }

    @Override
    public void setHeaderFilterStrategy(HeaderFilterStrategy headerFilterStrategy) {
        this.headerFilterStrategy = headerFilterStrategy;
    }


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

    public Boolean getCamelProxy() {
        return camelProxy;
    }

    public void setCamelProxy(Boolean camelProxy) {
        this.camelProxy = camelProxy;
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

    public boolean isThrowExceptionOnFailure() {
        return throwExceptionOnFailure;
    }

    public void setThrowExceptionOnFailure(boolean throwExceptionOnFailure) {
        this.throwExceptionOnFailure = throwExceptionOnFailure;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ResteasyHttpBinding getRestEasyHttpBinding() {
        return restEasyHttpBinding;
    }

    public void setRestEasyHttpBinding(ResteasyHttpBinding restEasyHttpBinding) {
        this.restEasyHttpBinding = restEasyHttpBinding;
    }


}
