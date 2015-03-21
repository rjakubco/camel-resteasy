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

import java.util.Set;

/**
 * Keeps track of HttpConsumers and ResteasyServlets and
 * connects them to each other.
 *
 */
public interface HttpRegistry {

    /**
     *
     * @param consumer
     */
    void register(HttpConsumer consumer);

    /**
     *
     * @param consumer
     */
    void unregister(HttpConsumer consumer);

    /**
     *
     * @param provider
     */
    void register(ResteasyCamelServlet provider);

    /**
     *
     * @param provider
     */
    void unregister(ResteasyCamelServlet provider);

    /**
     *
     * @param servletName
     * @return
     */
    ResteasyCamelServlet getCamelServlet(String servletName);
    // TODO probably useless
//    public Set<HttpConsumer> getConsumers();

}