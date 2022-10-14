package sloth.basic;

import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.extension.protocolplugin.Protocol;
import sloth.basic.http.HTTPQoSObserver;
import sloth.basic.http.error.HTTPErrorHandler;
import sloth.basic.handler.ServerRequestHandler;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.http.HTTPInvoker;
import sloth.basic.http.HTTPMarshaller;
import sloth.basic.protocols.TCPProtocol;
import sloth.basic.protocols.UDPProtocol;

public class Sloth {

    private final Protocol protocol = new UDPProtocol();
    private final ServerRequestHandler<HTTPRequest, HTTPResponse> serverRequestHandler = new ServerRequestHandler<>();
    private final HTTPMarshaller marshaller = new HTTPMarshaller();
    private final HTTPInvoker invoker = new HTTPInvoker();
    private final HTTPErrorHandler errorHandler = new HTTPErrorHandler();
    private final HTTPQoSObserver qoSObserver = new HTTPQoSObserver();

    public void init(int port) {
        invoker.registerRoutes(qoSObserver);
        serverRequestHandler.init(port, protocol, marshaller, invoker, errorHandler, qoSObserver);
    }

    public void registerRoutes(Object object) throws MiddlewareConfigurationException {
        invoker.registerRoutes(object);
    }

    public void registerConf(InvocationInterceptor<HTTPRequest, HTTPResponse> ext) {
        invoker.registerConf(ext);
    }
}
