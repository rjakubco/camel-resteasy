package org.apache.camel.component.resteasy.servlet;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class for copying input stream from HttpRequest
 *
 * @author : Roman Jakubco (rjakubco@redhat.com)
 */
public class ResteasyServletInputStreamCopier extends ServletInputStream {
    private InputStream input;
    private ByteArrayOutputStream copy;



    public ResteasyServletInputStreamCopier(InputStream inputStream) {
      /* create a new input stream from the cached request body */
        this.input = inputStream;
        this.copy = new ByteArrayOutputStream();
    }

    @Override
    public int read() throws IOException {
        int i = input.read();
        if(i > 0){
            copy.write(i);
        }
        return i;

    }
    public byte[] getCopy() {
        return copy.toByteArray();
    }

    public ByteArrayOutputStream getStream(){
        return copy;
    }
}
