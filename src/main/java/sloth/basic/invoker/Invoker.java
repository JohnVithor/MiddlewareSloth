package sloth.basic.invoker;


import sloth.basic.error.RemotingException;

public interface Invoker<Request, Response> {

    void beforeInvoke(Request request) throws RemotingException;
    Response invoke(Request request) throws RemotingException;
    void afterInvoke(Request request, Response response) throws RemotingException;
    void registerRoutes(Object object);
    void registerConf(InvocationInterceptor conf);
}
