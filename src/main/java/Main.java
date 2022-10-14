import sloth.basic.Sloth;
import sloth.basic.annotations.route.Body;
import sloth.basic.annotations.route.MethodMapping;
import sloth.basic.annotations.route.RequestMapping;
import sloth.basic.annotations.route.Param;
import sloth.basic.error.MiddlewareConfigurationException;
import sloth.basic.error.RemotingException;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.http.data.MethodHTTP;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.http.data.ContentType;
import sloth.basic.qos.QoSObserver;
import sloth.basic.qos.RouteStats;

import java.util.concurrent.atomic.AtomicInteger;
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
                throw new RemotingException(403, "Não tem header oi!");
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
            return 1;
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
                if (response.getStatusCode() != 200) {
                    if (response.getStatusCode() != 503 &&
                        stats.getConsecutiveErrorCount() >= 5 &&
                        negate.longValue() == 0) {
                        negate.add(5);
                    }
                }
            }
        }
    }

    @RequestMapping(path = "teste")
    public static class test {

        public static class testBody {
            public String field;

            public Double value;

            public testBody(String field, Double value) {
                this.field = field;
                this.value = value;
            }

            public void setField(String field) {
                this.field = field;
            }

            public String getField() {
                return field;
            }

            public Double getValue() {
                return value;
            }

            public void setValue(Double value) {
                this.value = value;
            }
        }

        @MethodMapping(method = MethodHTTP.GET, content_type = ContentType.HTML)
        public Integer testando(@Param(name = "p1") Integer test) {
            return test+test;
        }

        @MethodMapping(method = MethodHTTP.POST, content_type = ContentType.JSON)
        public testBody testando(@Param(name = "p1") Double test, @Body String asd) {
            return new testBody(asd, test);
        }
    }

    public static void main(String[] args) throws MiddlewareConfigurationException {
        Sloth sloth = new Sloth();
        sloth.activateQoS();
        sloth.registerConf(new ext());
        sloth.registerConf(new qos());
        sloth.registerRoutes(new test());
        sloth.init(8080);
    }
}