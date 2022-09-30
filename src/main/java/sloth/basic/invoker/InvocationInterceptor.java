package sloth.basic.invoker;

import sloth.basic.error.RemotingException;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;

public interface InvocationInterceptor extends Comparable<InvocationInterceptor>{

    int getPriority();

    void beforeRequest(HTTPRequest request) throws RemotingException;

    void afterResponse(HTTPRequest request, HTTPResponse response) throws RemotingException;

    default int compareTo(InvocationInterceptor o) {
        return Integer.compare(getPriority(), o.getPriority());
    }
}
