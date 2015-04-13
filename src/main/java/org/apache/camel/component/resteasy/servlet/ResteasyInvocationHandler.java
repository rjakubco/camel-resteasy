package org.apache.camel.component.resteasy.servlet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Implementation of InvocationHandler interface used for dynamic proxy invocation of methods.
 * We don't really need to do something dynamic so just null is returned.
 *
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public class ResteasyInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
