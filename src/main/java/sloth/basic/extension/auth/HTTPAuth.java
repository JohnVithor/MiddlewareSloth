package sloth.basic.extension.auth;

import sloth.basic.error.RemotingException;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.extension.RegistrationConfiguration;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.qos.RouteStats;


public interface HTTPAuth
        extends RegistrationConfiguration<HTTPRequest, HTTPResponse>,
        InvocationInterceptor<HTTPRequest, HTTPResponse> {

    boolean check(String route);

    @Override
    default int getPriority() {
        return 0;
    }
    @Override
    default void beforeRequest(HTTPRequest request, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) throws RemotingException {
        if (request != null && check(request.query())){
            if (request.headers().containsKey("Authorization")) {
                String auth = request.headers().get("Authorization");
                if (auth.equals("ADMIN")) {
                    return;
                }
                // TODO
            }
            throw new RemotingException(403, "Unauthorized");
        }
    }
    @Override
    default void afterResponse(HTTPRequest request, HTTPResponse response, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) {
        if (request != null && response != null && request.query().equals("/auth/login")) {
            response.getHeaders().put("Authorization", response.getBody());
            response.setBody("");
            response.getHeaders().put("Content-Length", "0");
        }
    }

    Credentials authenticate(String username, String password) throws AuthException;

}
