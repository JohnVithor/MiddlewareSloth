package sloth.basic.invoker;


import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.error.RemotingException;

import java.util.List;
import java.util.stream.Stream;

public interface Invoker<Request, Response> {

    Stream<InvocationInterceptor<Request, Response>> getHooks();
    default void beforeInvoke(Request request) throws RemotingException {
        for (InvocationInterceptor<Request, Response> e: getHooks().toList()) {
            e.beforeRequest(request);
        }
    }
    Response invoke(Request request) throws RemotingException;
    default void afterInvoke(Request request, Response response) throws RemotingException {
        for (InvocationInterceptor<Request, Response> e: getHooks().toList()) {
            e.afterResponse(request, response);
        }
    }
    void registerRoutes(Object object) throws MiddlewareConfigurationException;
    void registerConf(InvocationInterceptor<Request, Response> conf);
}
