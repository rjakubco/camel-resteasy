//package org.apache.camel.component.resteasy.test;
//
//import com.meterware.httpunit.HttpUnitOptions;
//import com.meterware.servletunit.ServletRunner;
//import com.meterware.servletunit.ServletUnitClient;
//import org.apache.camel.test.junit4.CamelTestSupport;
//import org.junit.After;
//import org.junit.Before;
//
//import java.io.InputStream;
//
///**
// * Created by roman on 24/10/14.
// */
//public class ResteasyCamelTestSupport extends CamelTestSupport {
//
//    public static final String CONTEXT = "/mycontext";
//    public static final String CONTEXT_URL = "http://localhost/mycontext";
//    protected ServletRunner sr;
//    protected boolean startCamelContext = true;
//
//    @Before
//    public void setUp() throws Exception {
//        InputStream is = this.getClass().getResourceAsStream(getConfiguration());
//        assertNotNull("The configuration input stream should not be null", is);
//        sr = new ServletRunner(is, CONTEXT);
//        sr.
//
//        HttpUnitOptions.setExceptionsThrownOnErrorStatus(true);
//        if (startCamelContext) {
//            super.setUp();
//        }
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        if (startCamelContext) {
//            super.tearDown();
//        }
//        sr.shutDown();
//    }
//
//    /**
//     * @return The web.xml to use for testing.
//     */
//    protected String getConfiguration() {
//        return "/org/apache/camel/component/servlet/web.xml";
//    }
//
//    protected ServletUnitClient newClient() {
//        return sr.newClient();
//    }
//}
