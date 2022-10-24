package sloth.basic;

import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.extension.auth.SimpleAuth;
import sloth.basic.extension.logging.HTTPRequestResponseLogger;
import sloth.basic.extension.logging.Logger;
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
import sloth.basic.qos.DisabledQoSObserver;
import sloth.basic.qos.QoSObserver;

import java.io.*;


public class Sloth {
    private final ServerRequestHandler<HTTPRequest, HTTPResponse> serverRequestHandler = new ServerRequestHandler<>();
    private final HTTPMarshaller marshaller = new HTTPMarshaller();
    private final HTTPInvoker invoker = new HTTPInvoker();
    private final HTTPErrorHandler errorHandler = new HTTPErrorHandler();
    private QoSObserver qoSObserver = new DisabledQoSObserver();

    public void activateQoS() {
        qoSObserver = new HTTPQoSObserver();
        invoker.registerRoutes(qoSObserver);
    }

    public void activateReqResLogging() {
        try {
            HTTPRequestResponseLogger logging = new HTTPRequestResponseLogger("./logging.output");
            activateCustomLogging(logging);
        } catch (IOException e) {
            System.err.println("Cannot start default logging");
        }
    }

    public void activateCustomLogging(Logger<HTTPRequest, HTTPResponse> logger) {
        logger.init();
        invoker.registerInterceptor(logger);
    }

    public void init(int port, Protocol protocol) {
        serverRequestHandler.init(port, protocol, marshaller, invoker, errorHandler, qoSObserver);
    }

    public void init(int port) {
        serverRequestHandler.init(port, new TCPProtocol(), marshaller, invoker, errorHandler, qoSObserver);
    }

    public void registerRoutes(Object object) throws MiddlewareConfigurationException {
        invoker.registerRoutes(object);
    }

    public void registerInterceptor(InvocationInterceptor<HTTPRequest, HTTPResponse> ext) {
        invoker.registerInterceptor(ext);
    }

    public void registerAuth(SimpleAuth auth) {
        invoker.configure(auth);
        invoker.registerInterceptor(auth);
        invoker.registerRoutes(auth);
    }
}
