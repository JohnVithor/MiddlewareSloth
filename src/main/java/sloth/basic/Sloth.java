package sloth.basic;

import sloth.basic.handler.ServerRequestHandler;
import sloth.basic.invoker.InvocationInterceptor;
import sloth.basic.invoker.HTTPInvoker;

public class Sloth {

    private final HTTPInvoker invoker = new HTTPInvoker();

    public void init(int port) {
        ServerRequestHandler.init(port, invoker);
    }

    public void registerRoutes(Object object) {
        invoker.registerRoutes(object);
    }

    public void registerConf(InvocationInterceptor ext) {
        invoker.registerConf(ext);
    }
}
