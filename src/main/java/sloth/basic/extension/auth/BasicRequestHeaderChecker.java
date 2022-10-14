package sloth.basic.extension.auth;

import sloth.basic.error.RemotingException;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.qos.QoSObserver;
import sloth.basic.qos.RouteStats;

public class BasicRequestHeaderChecker implements InvocationInterceptor<HTTPRequest, HTTPResponse>{

    private final String header;
    private final int priority;

    public BasicRequestHeaderChecker(String header, int priority) {
        this.header = header;
        this.priority = priority;
    }
    @Override
    public int getPriority() {
        return priority;
    }
    @Override
    public void beforeRequest(HTTPRequest request, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) throws RemotingException {
        if (!request.getHeaders().containsKey(header)) {

        } else {

        }
    }
    @Override
    public void afterResponse(HTTPRequest request, HTTPResponse response, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) {
        // EMPTY
    }
}
