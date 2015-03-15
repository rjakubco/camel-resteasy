package org.apache.camel.component.resteasy.servlet;

import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * Created by Roman Jakubco (rjakubco@redhat.com) on 14/03/15.
 */
public class ResteasyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private ServletInputStream inputStream;
    private BufferedReader reader;
    private ResteasyServletInputStreamCopier copier;

    public ResteasyHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (inputStream == null) {
            inputStream = getRequest().getInputStream();
            copier = new ResteasyServletInputStreamCopier(inputStream);
        }

        return copier;
    }

    @Override
    public BufferedReader getReader() throws IOException{
        if (inputStream != null) {
            throw new IllegalStateException("getInputStream() has already been called on this response.");
        }
        if (reader == null) {
            copier = new ResteasyServletInputStreamCopier(getRequest().getInputStream());
            reader = new BufferedReader(new InputStreamReader(copier, getRequest().getCharacterEncoding()));
        }

        return reader;


    }
    public byte[] getCopy() {
        if (copier != null) {
            return copier.getCopy();
        } else {
            return new byte[0];
        }
    }

    public ByteArrayOutputStream getStream(){
        if (copier != null) {
            return copier.getStream();
        } else {
            return null;
        }
    }
}
