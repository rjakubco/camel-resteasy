package org.apache.camel.component.resteasy.servlet;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Customer Filter used for wrapping requests and responses to created custom wrappers.
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
@Provider
@WebFilter("/*")
public class ResteasyFilter implements Filter {

    @Override
    public void init(FilterConfig config) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (response.getCharacterEncoding() == null) {
            response.setCharacterEncoding("UTF-8"); // Or whatever default. UTF-8 is good for World Domination.
        }
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8"); // Or whatever default. UTF-8 is good for World Domination.
        }

        ResteasyHttpServletResponseWrapper responseCopier = new ResteasyHttpServletResponseWrapper((HttpServletResponse) response);
        ResteasyHttpServletRequestWrapper requestCopier = new ResteasyHttpServletRequestWrapper((HttpServletRequest) request);

        chain.doFilter(requestCopier, responseCopier);

        responseCopier.flushBuffer();

    }

    @Override
    public void destroy() {

    }


}
