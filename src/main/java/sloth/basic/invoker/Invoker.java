package sloth.basic.invoker;


import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.error.RemotingException;
import sloth.basic.extension.RegistrationConfiguration;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.http.util.RouteInfos;
import sloth.basic.marshaller.IdentifiedSizeable;
import sloth.basic.qos.RouteStats;

import java.util.PriorityQueue;

public abstract class Invoker<Request extends IdentifiedSizeable, Response extends IdentifiedSizeable> {

    private final PriorityQueue<InvocationInterceptor<Request, Response>> hooks = new PriorityQueue<>();
    private final PriorityQueue<RegistrationConfiguration<Request, Response>> registrationConfigurations = new PriorityQueue<>();
    public void beforeInvoke(Request request, RouteStats<Request, Response> stats) throws RemotingException {
        for (InvocationInterceptor<Request, Response> e: hooks) {
            e.beforeRequest(request, stats);
        }
    }
    public abstract Response invoke(Request request) throws RemotingException;
    public void afterInvoke(Request request, Response response, RouteStats<Request, Response> stats) throws RemotingException {
        for (InvocationInterceptor<Request, Response> e: hooks) {
            e.afterResponse(request, response, stats);
        }
    }
    public void registerInterceptor(InvocationInterceptor<Request, Response> conf) {
        hooks.add(conf);
    }
    public abstract void registerRoutes(Object object) throws MiddlewareConfigurationException;

    public void configure(RegistrationConfiguration<Request, Response> registrationConfiguration) {
        registrationConfigurations.add(registrationConfiguration);
    }
    public void configurationsNotify(String route, RouteInfos methods) {
        for (RegistrationConfiguration<Request, Response> c: registrationConfigurations) {
            c.consume(route, methods);
        }
    }
}
