package org.apache.camel.component.resteasy;

import org.apache.camel.Exchange;
import org.apache.camel.InvalidPayloadException;
import org.apache.camel.Message;
import org.apache.camel.component.http.DefaultHttpBinding;
import org.apache.camel.component.http.HttpConstants;
import org.apache.camel.component.http.HttpEndpoint;
import org.apache.camel.component.http.HttpHeaderFilterStrategy;
import org.apache.camel.component.http.helper.HttpHelper;
import org.apache.camel.converter.stream.CachedOutputStream;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.util.GZIPHelper;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.MessageHelper;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by roman on 26/10/14.
 */
public class RestEasyHttpBinding extends DefaultHttpBinding {
    private static final Logger LOG = LoggerFactory.getLogger(RestEasyHttpBinding.class);
    private HeaderFilterStrategy headerFilterStrategy = new HttpHeaderFilterStrategy();

    public RestEasyHttpBinding(HttpEndpoint endpoint) {
        super(endpoint);
        this.headerFilterStrategy = endpoint.getHeaderFilterStrategy();
    }

    @Override
    public void doWriteResponse(Message message, HttpServletResponse response, Exchange exchange) throws IOException {
        // set the status code in the response. Default is 200.
        System.out.println("doWriteResponse");
        if (message.getHeader(Exchange.HTTP_RESPONSE_CODE) != null) {
            int code = message.getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);
            response.setStatus(code);
        }
        // set the content type in the response.
        String contentType = MessageHelper.getContentType(message);
        if (contentType != null) {
            response.setContentType(contentType);
        }

        // append headers
        // must use entrySet to ensure case of keys is preserved
        for (Map.Entry<String, Object> entry : message.getHeaders().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // use an iterator as there can be multiple values. (must not use a delimiter)
            final Iterator<?> it = ObjectHelper.createIterator(value, null);
            while (it.hasNext()) {
                String headerValue = exchange.getContext().getTypeConverter().convertTo(String.class, it.next());
                if (headerValue != null && headerFilterStrategy != null
                        && !headerFilterStrategy.applyFilterToCamelHeaders(key, headerValue, exchange)) {
                    response.addHeader(key, headerValue);
                }
            }
        }

        // write the body.
        if (message.getBody() != null) {
            if (GZIPHelper.isGzip(message)) {
                System.out.println("GZIP");
                doWriteGZIPResponse(message, response, exchange);
            } else {
                System.out.println("Direct");
                doWriteDirectResponse(message, response, exchange);
            }
        }
    }

    @Override
    protected void doWriteDirectResponse(Message message, HttpServletResponse response, Exchange exchange) throws IOException {
        // if content type is serialized Java object, then serialize and write it to the response
        String contentType = message.getHeader(Exchange.CONTENT_TYPE, String.class);
        if (contentType != null && HttpConstants.CONTENT_TYPE_JAVA_SERIALIZED_OBJECT.equals(contentType)) {
            try {
                Object object = message.getMandatoryBody(Serializable.class);
                HttpHelper.writeObjectToServletResponse(response, object);
                // object is written so return
                return;
            } catch (InvalidPayloadException e) {
                throw new IOException(e);
            }
        }

        // prefer streaming
        InputStream is = null;
        if (checkChunked(message, exchange)) {
            System.out.println("is chunked");
            is = message.getBody(InputStream.class);
        } else {
            // try to use input stream first, so we can copy directly
            if (!isText(contentType)) {
                is = exchange.getContext().getTypeConverter().tryConvertTo(InputStream.class, message.getBody());
            }
        }

        if (is != null) {
            System.out.println("InputStream nie je null");
            ServletOutputStream os = response.getOutputStream();
            if (!checkChunked(message, exchange)) {
                CachedOutputStream stream = new CachedOutputStream(exchange);
                try {
                    // copy directly from input stream to the cached output stream to get the content length
                    int len = copyStream(is, stream, response.getBufferSize());
                    // we need to setup the length if message is not chucked
                    response.setContentLength(len);
                    OutputStream current = stream.getCurrentStream();
                    if (current instanceof ByteArrayOutputStream) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Streaming (direct) response in non-chunked mode with content-length {}");
                        }
                        ByteArrayOutputStream bos = (ByteArrayOutputStream) current;
                        bos.writeTo(os);
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Streaming response in non-chunked mode with content-length {} and buffer size: {}", len, len);
                        }
                        copyStream(stream.getInputStream(), os, len);
                    }
                } finally {
                    IOHelper.close(is, os);
                }
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Streaming response in chunked mode with buffer size {}", response.getBufferSize());
                }
                copyStream(is, os, response.getBufferSize());
            }
        } else {
            System.out.println("is je null");
            // not convertable as a stream so fallback as a String
            String data = message.getBody(String.class);
            if (data != null) {
                // set content length and encoding before we write data
                String charset = IOHelper.getCharsetName(exchange, true);
                final int dataByteLength = data.getBytes(charset).length;
                response.setCharacterEncoding(charset);
                response.setContentLength(dataByteLength);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Writing response in non-chunked mode as plain text with content-length {} and buffer size: {}", dataByteLength, response.getBufferSize());
                }
                try {
                    response.getWriter().print(data);
                } finally {
                    response.getWriter().flush();
                }
            }
        }
    }
}
