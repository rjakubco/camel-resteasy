camel-resteasy
==============
Resteasy camel component
* Component is still in development so at the moment it is not included in master branch of Camel. 

### Producer:
Producer is using RESTEasy implementation of JAX-RS 2.0 Client API for calling server. The producer will use all possibilities of RESTEasy client along with Resteasy Proxy Framework ([see more](http://docs.jboss.org/resteasy/docs/3.0-beta-3/userguide/html/RESTEasy_Client_Framework.html))

Uri options for producer (can change in the future):
* throwExceptionOnFailure
* username -> username for basic authentication
* password -> password for basic authentication
* resteasyMethod (String)-> GET/POST/DELETE/PUT 
* proxyClientClass (String) -> full name of the interface, which should be used for proxy client
* proxyMethod (String) -> name of the method, which should be invoked on specified proxyClientClass


#### Usage:
It is pretty straight forward just addd camel-resteasy dependecy to your project and you can use it for client calls.
#####Basic examples:
``` 
<route>
    <from uri="direct:start"/>
    <to uri="resteasy:http://localhost:8080/RESTfulDemoApplication/user-management/users/1"/>
</route>
``` 
Http method use for call can be send as Exchange.HTTP_METHOD header in message or specified by uri option "resteasyMethod".
```
<route>
    <from uri="direct:start"/>
    <to uri="resteasy:http://localhost:8080/RESTfulDemoApplication/user-management/users?resteasyMethod=POST"/>
</route>

<route>
     <from uri="resteasy:///test/match/print?servletName=resteasy-camel-servlet"/>
     <to uri="resteasy:http://localhost:8080?proxyClientClass=com.proxy.rest.TestProxyClass&amp;proxyMethod=testingProxyMethod"/>
 </route>
``` 
### Consumer:
Consumer is representing the server side of the RESTEasy and it is integrated with RESTEasy be extending HttpServletDispatcher class and implementing own servlet. The servlet will find all classes with RESTEasy annotations and handle all calls to them. Message sent to camel route will contain stream in its body representing returned response entity. The message will be also containing request and response as headers. Camel route specified for the consumer will be executed before the response is send back to the client.

RESTEasy is only registering annotations in classes or on interfaces if they have implementing class. If you want just specified paths and methods by annotation in interface and handle creating response in camel, you can use this component with proxy option. You just need to specify proxy interfaces in component bean in property "proxyConsumersClasses", separated by comma.
Then set URI option "proxy" on consumer to true The component will create dynamic proxy classes implementing these interfaces and register them with the servlet. This is done on initialization of the servlet.

There is also posibility to just create consumers in camel route without interface or classes. For this purporse just set URI option camelProxy to true and you can create responses in camel. You can also sey URI option httpMethodRestrict to restrict which http method will be allowed.
``` 
<bean id="resteasy" class="org.apache.camel.component.resteasy.ResteasyComponent">
        <property name="proxyConsumersClasses" value="com.camel.rest.ServiceInterface,com.camel.rest.AnotherServiceInterface"/>
</bean>
``` 

Uri options for consumer (can change in the future):
* matchOnUriPrefix        -> Whether or not the CamelServlet should try to find a target consumer by matching the URI prefix, if no exact match is found. 
* servlet-name (required) -> Specifies the servlet name that the servlet endpoint will bind to. This name should match the name you define in web.xml file. 
* proxy (boolean) -> if set to true, then camel with register this consumer as proxy.
* camelProxy(boolean) -> if set to true, then this address is only specified in Camel.
* httpMethodRestrict ->  Used to only allow consuming if the HttpMethod matches, such as GET/POST/PUT etc.


#### Usage:
As always add depenecy of camel-resteasy in to your project.

If you want to use consumer your need to use servlet created for CamelResteasy. CamelResteasy has also its own filter
needed for consuming messages and this filter is added to configuration by @WebFilter annotation. To this you need to
add this configuration in to the web.xml:
``` 

<servlet>
	<servlet-name>resteasy-camel-servlet</servlet-name>
	<servlet-class>org.apache.camel.component.resteasy.servlet.ResteasyCamelServlet</servlet-class>
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
              <from uri="resteasy:/customer/print?servletName=resteasy-camel-servlet"/>
                <to uri="bean:process"/>
            </route>           

            <route>
                <from uri="resteasy:/customer/match?servletName=resteasy-camel-servlet&amp;matchOnUriPrefix=true"/>
                <setBody>
                    <constant>match</constant>
                </setBody>
           </route>   
           
           <route>
                <from uri="resteasy:/only/camel/address?servletName=resteasy-camel-servlet&amp;camelProxy=true"/>
                <setBody>
                    <constant>Creation of consumer without class or interface was successful</constant>
                </setBody>
            </route>
            <route>
                <from uri="resteasy:/address/specified/in/interface?servletName=resteasy-camel-servlet&amp;proxy=true"/>
                <setBody>
                    <constant>Creation of resteasy address from interface com.camel.rest.ServiceInterface by dynamic
                    proxy</constant>
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
Simple example of the interface used for proxy consumer example in the code above.
``` 
package com.camel.rest;

@Path("/address")
public interface ServiceInterface {

    @GET
    @Path("/specified/in/interface")
    public Response printMessage();



    @POST
    @Path("/post")
    @Consumes("application/json")
    public Response createProductInJSON(Product product);
}

}
``` 
