package org.apache.camel.component.resteasy;

import org.apache.camel.impl.DefaultHeaderFilterStrategy;

/**
 * Created by roman on 20/03/15.
 */
public class ResteasyHeaderFilterStrategy extends DefaultHeaderFilterStrategy {
    public ResteasyHeaderFilterStrategy() {
        initialize();
    }

    protected void initialize() {
        getOutFilter().add("content-length");
        getOutFilter().add("content-type");
        getOutFilter().add("host");
        // Add the filter for the Generic Message header
        // http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.5
        getOutFilter().add("cache-control");
        getOutFilter().add("connection");
        getOutFilter().add("transfer-encoding");
        getOutFilter().add("upgrade");
        getOutFilter().add("via");
        getOutFilter().add("warning");

        setLowerCase(true);
//
//        // filter headers begin with "Camel" or "org.apache.camel"
//        // must ignore case for Http based transports
        setOutFilterPattern("(?i)(Camel|org\\.apache\\.camel)[\\.|a-z|A-z|0-9]*");
    }
}
