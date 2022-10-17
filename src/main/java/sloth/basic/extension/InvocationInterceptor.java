package sloth.basic.extension;

import sloth.basic.error.RemotingException;
import sloth.basic.marshaller.IdentifiedSizeable;
import sloth.basic.qos.RouteStats;

public interface InvocationInterceptor<Request extends IdentifiedSizeable, Response extends IdentifiedSizeable> extends Comparable<InvocationInterceptor<Request, Response>>{

    int getPriority();

    void beforeRequest(Request request, RouteStats<Request, Response> qoSObserver) throws RemotingException;

    void afterResponse(Request request, Response response, RouteStats<Request, Response> qoSObserver) throws RemotingException;

    default int compareTo(InvocationInterceptor o) {
        return Integer.compare(getPriority(), o.getPriority());
    }
}
