import sloth.basic.Sloth;
import sloth.basic.annotations.route.Body;
import sloth.basic.annotations.route.MethodMapping;
import sloth.basic.annotations.route.RequestMapping;
import sloth.basic.annotations.route.Param;
import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.error.RemotingException;
import sloth.basic.extension.auth.SimpleAuth;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.http.data.MethodHTTP;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.http.data.ContentType;
import sloth.basic.qos.RouteStats;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

public class Main {

    public static class ext implements InvocationInterceptor<HTTPRequest,HTTPResponse> {

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public void beforeRequest(HTTPRequest request, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) throws RemotingException {
            if (!request.getHeaders().containsKey("oi")) {
                throw new RemotingException(400, "Não tem header oi!");
            }
        }

        @Override
        public void afterResponse(HTTPRequest request, HTTPResponse response, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) throws RemotingException {
            response.getHeaders().put("OI", "adicionado automaticamente");
        }
    }

    public static class qos implements InvocationInterceptor<HTTPRequest,HTTPResponse> {
        private final LongAdder negate = new LongAdder();
        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public void beforeRequest(HTTPRequest request, RouteStats<HTTPRequest, HTTPResponse> stats) throws RemotingException {
            if(request.getQuery().equals("/teste")) {
                if (negate.longValue() > 0) {
                    negate.decrement();
                    throw new RemotingException(503, "Service Unavailable");
                }
            }
        }

        @Override
        public void afterResponse(HTTPRequest request, HTTPResponse response, RouteStats<HTTPRequest, HTTPResponse> stats) throws RemotingException {
            if(request.getQuery().equals("/teste")) {
                if (response.getStatusCode() != 200 &&
                    response.getStatusCode() != 503 &&
                    stats.getConsecutiveErrorCount() >= 5 &&
                    negate.longValue() == 0) {
                    negate.add(5);
                }
            }
        }
    }

    @RequestMapping(path = "teste")
    public static class test {

        public static class testBody<T> {
            public T field;

            public testBody() {
            }

            public testBody(T field) {
                this.field = field;
            }

            public void setField(T field) {
                this.field = field;
            }

            public T getField() {
                return field;
            }
        }

        @MethodMapping(method = MethodHTTP.GET, content_type = ContentType.HTML)
        public Integer testando(@Param(name = "p1") List<Integer> test) {
            return test.size();
        }

        @MethodMapping(method = MethodHTTP.POST, content_type = ContentType.JSON)
        public testBody<Integer> testando(@Body testBody<Integer> asd) {
            return new testBody<>(asd.field);
        }
    }

    public static void main(String[] args) throws MiddlewareConfigurationException {
        Sloth sloth = new Sloth();
        sloth.activateQoS();
        sloth.activateReqResLogging();
//        sloth.registerAuth(new SimpleAuth());
//        sloth.registerInterceptor(new ext());
//        sloth.registerInterceptor(new qos());
        sloth.registerRoutes(new test());
        sloth.init(8080);
    }
}