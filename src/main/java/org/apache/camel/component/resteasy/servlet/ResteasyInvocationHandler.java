package org.apache.camel.component.resteasy.servlet;

import javax.ws.rs.core.Response;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class ResteasyInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
