camel-resteasy
==============
Resteasy camel component
* Component is still in development so at the moment it is not included in master branch of Camel. 

### Producer:
Producer is using RESTEasy implementation of JAX-RS 2.0 Client API for calling server. The producer will use all possibilities of RESTEasy client along with Resteasy Proxy Framework ([see more](http://docs.jboss.org/resteasy/docs/3.0-beta-3/userguide/html/RESTEasy_Client_Framework.html))

Uri options for producer (can change in the future):
* throwExceptionOnFailure
* login
* password
* resteasyMethod (String)-> GET/POST/DELETE/PUT 
* proxyClasses (boolean) -> if proxy framework should be use for the client.
* OauthSecure (boolean)  -> false by default -> component check if http or https protocol is used. If https is detected then login and password should be provided as headers or as uri options.
		         -> if set true then OAuth authorization is used and required headers must be specified.

Most of the options are not yet implemented!

#### Usage:
It is pretty straight forward just addd camel-resteasy dependecy to your project and you can use it for client calls.
#####Basic examples:
``` 
<route>
    <from uri="direct:start"/>
    <to uri="resteasy:http://localhost:8080/RESTfulDemoApplication/user-management/users/1"/>
</route>
``` 
Http method use for call can be send as Exchange.HTTP_METHOD header in message or specified by uri option "resteasyMethod"
``` 
<route>
    <from uri="direct:start"/>
    <to uri="resteasy:http://localhost:8080/RESTfulDemoApplication/user-management/users?resteasyMethod=POST"/>
</route>
``` 

### Consumer:
Consumer is representing the server side of the RESTEasy and it is integrated with RESTEasy be extending HttpServletDispatcher class and implementing own servlet. The servlet will find all classes with RESTEasy annotations and handle all calls to them. Message sent to camel route will contain stream in its body representing returned response entity. The message will be also containing request and response as headers. Camel route specified for the consumer will be executed before the response is send back to the client.

RESTEasy is only registering annotations in classes or on interfaces if they have implementing class. If you want just specified paths and methods by annotation in interface and handle creating response in camel and you can use this component with proxy option, which will find all interfaces with resteasy annotations and creates dynamic proxy classes implementing these interfaces and register them with the servlet. This is done on initialization of the servlet. Finding these interfaces in classloader on bigger project can be really slow, so bear that in mind. 


Uri options for consumer (can change in the future):
* matchOnUriPrefix        -> Whether or not the CamelServlet should try to find a target consumer by matching the URI prefix, if no exact match is found. 
* servlet-name (required) -> Specifies the servlet name that the servlet endpoint will bind to. This name should match the name you define in web.xml file. 


#### Usage:
As always add depenecy of camel-resteasy in to your project.

If you want to use consumer your need to use servlet and filter implemeneted in the component.  To this you need to add this configuration in to the web.xml:
``` 
<filter>
       <filter-name>Camel Response filter</filter-name>
       <filter-class>org.apache.camel.component.resteasy.servlet.RESTEasyResponseFilter</filter-class>
</filter>
<filter-mapping>
       <filter-name>Camel Response filter</filter-name>
       <servlet-name>resteasy-camel-servlet</servlet-name>
</filter-mapping>

<servlet>
	<servlet-name>resteasy-camel-servlet</servlet-name>
	<servlet-class>org.apache.camel.component.resteasy.servlet.RESTEasyCamelServlet</servlet-class>
</servlet>

<servlet-mapping>
	<servlet-name>resteasy-camel-servlet</servlet-name>
	<url-pattern>Specified your url pattern</url-pattern>
</servlet-mapping>
``` 


After that just create the component and use it as consumer in the camel route, exactly like in this simple example:
``` 
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:camel="http://camel.apache.org/schema/spring"
           xsi:schemaLocation="
         http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
         http://camel.apache.org/schema/spring http://camel.apache.org/schema/spring/camel-spring.xsd">

      <camelContext xmlns="http://camel.apache.org/schema/spring">
           <route>
              <from uri="resteasy:///customer/print?servletName=resteasy-camel-servlet"/>
                <to uri="bean:process"/>
            </route>           

            <route>
                <from uri="resteasy:///customer/match?servletName=resteasy-camel-servlet&amp;matchOnUriPrefix=true"/>
                <setBody>
                    <constant>match</constant>
                </setBody>
           </route>                    

      </camelContext>

    <bean id="process" class="com.mkyong.context.TestBean"/>

    <bean id="RestEasyComp" class="org.apache.camel.component.resteasy.RESTEasyComponent"/>
</beans>
``` 
And here is simple resteasy class, which is used in example above:
``` 
@Path("/customer")
public class PrintService {
     @GET
     @Path("/print")
     public Response printMessage() {
	return Response.status(200).entity("print Message").build();
     }


    @GET
    @Path("/match/print3")
    public Response printMessage3(){
        return Response.status(200).entity("/customer/test/print2").build();
    }
}
``` 
