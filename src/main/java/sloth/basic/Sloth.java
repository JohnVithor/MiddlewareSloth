package sloth.basic;

import sloth.basic.handler.ServerRequestHandler;
import sloth.basic.invoker.HTTPInvoker;

public class Sloth {

    private final HTTPInvoker invoker = new HTTPInvoker();

    public void init(int port) {
        ServerRequestHandler.init(port);
    }

    public void register(Object object) {
        invoker.register(object);
    }
}
