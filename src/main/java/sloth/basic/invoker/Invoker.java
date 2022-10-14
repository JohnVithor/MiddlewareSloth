package sloth.basic.invoker;


import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.error.RemotingException;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.marshaller.IdentifiedSizeable;
import sloth.basic.qos.QoSObserver;
import sloth.basic.qos.RouteStats;

import java.util.stream.Stream;

public interface Invoker<Request extends IdentifiedSizeable, Response extends IdentifiedSizeable> {

    Stream<InvocationInterceptor<Request, Response>> getHooks();
    default void beforeInvoke(Request request, RouteStats<Request, Response> stats) throws RemotingException {
        for (InvocationInterceptor<Request, Response> e: getHooks().toList()) {
            e.beforeRequest(request, stats);
        }
    }
    Response invoke(Request request) throws RemotingException;
    default void afterInvoke(Request request, Response response, RouteStats<Request, Response> stats) throws RemotingException {
        for (InvocationInterceptor<Request, Response> e: getHooks().toList()) {
            e.afterResponse(request, response, stats);
        }
    }
    void registerRoutes(Object object) throws MiddlewareConfigurationException;
    void registerConf(InvocationInterceptor<Request, Response> conf);
}
