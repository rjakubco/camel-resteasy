package org.apache.camel.component.resteasy.servlet;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class for copying output stream from HttpResponse
 *
 * @author : Roman Jakubco | rjakubco@redhat.com
 */
public class ResteasyServletOutputStreamCopier extends ServletOutputStream {

    private OutputStream outputStream;
    private ByteArrayOutputStream copy;

    public ResteasyServletOutputStreamCopier(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.copy = new ByteArrayOutputStream();
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        copy.write(b);
    }

    public byte[] getCopy() {
        return copy.toByteArray();
    }

    public ByteArrayOutputStream getStream(){
        return copy;
    }

}