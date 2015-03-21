package org.apache.camel.component.resteasy;

import org.apache.camel.impl.DefaultHeaderFilterStrategy;

/**
 * Default ResteasyHeaderFilterStrategy in this component.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com).
 */
public class ResteasyHeaderFilterStrategy extends DefaultHeaderFilterStrategy {

    public ResteasyHeaderFilterStrategy() {
        initialize();
    }

    protected void initialize() {
        getOutFilter().add("content-length");
        getOutFilter().add("content-type");
        getOutFilter().add("host");
        getOutFilter().add("cache-control");
        getOutFilter().add("connection");
        getOutFilter().add("transfer-encoding");
        getOutFilter().add("upgrade");
        getOutFilter().add("via");
        getOutFilter().add("warning");

        setLowerCase(true);

//     filter headers begin with "Camel" or "org.apache.camel"
//     must ignore case for Http based transports
        setOutFilterPattern("(?i)(Camel|org\\.apache\\.camel)[\\.|a-z|A-z|0-9]*");
    }
}
