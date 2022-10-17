package sloth.basic.extension.auth;

import sloth.basic.error.RemotingException;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.extension.RegistrationConfiguration;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.http.util.RouteInfos;
import sloth.basic.qos.RouteStats;

import java.util.HashSet;

public interface HTTPAuth extends RegistrationConfiguration<HTTPRequest, HTTPResponse>, InvocationInterceptor<HTTPRequest, HTTPResponse>{

    boolean check(String route);

    @Override
    default int getPriority() {
        return 0;
    }
    @Override
    default void beforeRequest(HTTPRequest request, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) throws RemotingException {
        if (check(request.getQuery())){
            if (!request.getHeaders().containsKey("Authorization")) {
                throw new RemotingException(403, "Unauthorized");
            }
        }
    }
    @Override
    default void afterResponse(HTTPRequest request, HTTPResponse response, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) {
        // EMPTY
    }
}
