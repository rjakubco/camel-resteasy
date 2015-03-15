package org.apache.camel.component.resteasy.servlet;

import javax.servlet.ServletInputStream;
import java.io.*;

/**
 * Created by Roman Jakubco (rjakubco@redhat.com) on 14/03/15.
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
        if(i > -1){
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
