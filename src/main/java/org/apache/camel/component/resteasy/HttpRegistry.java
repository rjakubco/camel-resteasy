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

import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.resteasy.servlet.ResteasyCamelServlet;

/**
 * Keeps track of HttpConsumers and ResteasyServlets and
 * connects them to each other.
 *
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public interface HttpRegistry {

    /**
     * Register HttpConsumer to the registry
     *
     * @param consumer to register
     */
    public void register(HttpConsumer consumer);

    /**
     * Unregister HttpConsumer from the registry
     *
     * @param consumer to unregister
     */
    public void unregister(HttpConsumer consumer);

    /**
     * Register ResteasyCamelServlet to the registry
     *
     * @param provider to register
     */
    public void register(ResteasyCamelServlet provider);

    /**
     * Unregister ResteasyCamelServlet from the registry
     *
     * @param provider to unregister
     */
    public void unregister(ResteasyCamelServlet provider);

    /**
     * Getter for ResteasyCamelServlet with given name used in RestCamelServlet
     *
     * @param servletName for the ResteasyCamelServlet to find
     * @return ResteasyCamel servlet with given name
     */
    public ResteasyCamelServlet getCamelServlet(String servletName);
}