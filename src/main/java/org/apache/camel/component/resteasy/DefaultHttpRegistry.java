/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.resteasy;

import org.apache.camel.component.http.CamelServlet;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.resteasy.servlet.RESTEasyCamelServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.*;

public class DefaultHttpRegistry implements HttpRegistry {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHttpRegistry.class);

    private static Map<String, HttpRegistry> registries = new HashMap<String, HttpRegistry>();
    
    private final Set<HttpConsumer> consumers;
    private final Set<RESTEasyCamelServlet> providers;

    public DefaultHttpRegistry() {
        consumers = new HashSet<HttpConsumer>();
        providers = new HashSet<RESTEasyCamelServlet>();
    }
    
    /**
     * Lookup or create a new registry if none exists with the given name
     */
    public static synchronized HttpRegistry getHttpRegistry(String name) {
        HttpRegistry answer = registries.get(name);
        if (answer == null) {
            answer = new DefaultHttpRegistry();
            registries.put(name, answer);
        }
        return answer;
    }

    /**
     * Removes the http registry with the given name
     */
    public static synchronized void removeHttpRegistry(String name) {
        registries.remove(name);
    }
    
    @Override
    public void register(HttpConsumer consumer) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering consumer for path {} providers present: {}", consumer.getPath(), providers.size());
        }
        consumers.add(consumer);
        for (RESTEasyCamelServlet provider : providers) {
            provider.connect(consumer);
        }
    }
    
    @Override
    public void unregister(HttpConsumer consumer) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Unregistering consumer for path {}", consumer.getPath());
        }
        consumers.remove(consumer);
        for (RESTEasyCamelServlet provider : providers) {
            provider.disconnect(consumer);
        }
    }
    
    @SuppressWarnings("rawtypes")
    public void register(RESTEasyCamelServlet provider, Map properties) {
        RESTEasyCamelServlet camelServlet = provider;
        camelServlet.setServletName((String) properties.get("servlet-name"));
        register(camelServlet);
    }

    public void unregister(RESTEasyCamelServlet provider, Map<String, Object> properties) {
        unregister(provider);
    }
    
    @Override
    public void register(RESTEasyCamelServlet provider) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering CamelServlet with name {} consumers present: {}", provider.getServletName(), consumers.size());
        }
        providers.add(provider);
        for (HttpConsumer consumer : consumers) {
            provider.connect(consumer);
        }
    }

    @Override
    public void unregister(RESTEasyCamelServlet provider) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Unregistering CamelServlet with name {}", provider.getServletName());
        }
        providers.remove(provider);
    }

    @Override
    public RESTEasyCamelServlet getCamelServlet(String servletName) {
        for (RESTEasyCamelServlet provider : providers) {
            if (provider.getServletName().equals(servletName)) {
                return provider;
            }
        }
        return null;
    }

    public void setServlets(List<Servlet> servlets) {
        providers.clear();
        for (Servlet servlet : servlets) {
            if (servlet instanceof CamelServlet) {
                providers.add((RESTEasyCamelServlet) servlet);
            }
        }
    }

    public Set<HttpConsumer> getConsumers() {
        return consumers;
    }
}
