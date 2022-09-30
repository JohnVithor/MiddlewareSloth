package sloth.basic;

import sloth.basic.error.HTTPErrorHandler;
import sloth.basic.handler.ServerRequestHandler;
import sloth.basic.http.HTTPRequest;
import sloth.basic.http.HTTPResponse;
import sloth.basic.invoker.InvocationInterceptor;
import sloth.basic.invoker.HTTPInvoker;
import sloth.basic.marshaller.HTTPMarshaller;

public class Sloth {

    private final ServerRequestHandler<HTTPRequest, HTTPResponse> serverRequestHandler = new ServerRequestHandler<>();
    private final HTTPMarshaller marshaller = new HTTPMarshaller();
    private final HTTPInvoker invoker = new HTTPInvoker();
    private final HTTPErrorHandler errorHandler = new HTTPErrorHandler();

    public void init(int port) {
        serverRequestHandler.init(port, marshaller, invoker, errorHandler);
    }

    public void registerRoutes(Object object) {
        invoker.registerRoutes(object);
    }

    public void registerConf(InvocationInterceptor ext) {
        invoker.registerConf(ext);
    }
}
