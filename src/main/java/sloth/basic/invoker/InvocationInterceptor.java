package sloth.basic.invoker;

import sloth.basic.error.RemotingException;

public interface InvocationInterceptor<Request, Response> extends Comparable<InvocationInterceptor>{

    int getPriority();

    void beforeRequest(Request request) throws RemotingException;

    void afterResponse(Request request, Response response) throws RemotingException;

    default int compareTo(InvocationInterceptor o) {
        return Integer.compare(getPriority(), o.getPriority());
    }
}
