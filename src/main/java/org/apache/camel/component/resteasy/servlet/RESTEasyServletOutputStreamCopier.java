package org.apache.camel.component.resteasy.servlet;

import javax.servlet.ServletOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by roman on 26/10/14.
 */
public class RESTEasyServletOutputStreamCopier extends ServletOutputStream {

    private OutputStream outputStream;
    private ByteArrayOutputStream copy;

    public RESTEasyServletOutputStreamCopier(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.copy = new ByteArrayOutputStream(1024);
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