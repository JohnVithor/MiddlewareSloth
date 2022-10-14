package sloth.basic.http;

import sloth.basic.annotations.route.MethodMapping;
import sloth.basic.annotations.route.Param;
import sloth.basic.annotations.route.RequestMapping;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.http.data.MethodHTTP;
import sloth.basic.http.qos.HTTPQoSData;
import sloth.basic.qos.QoSData;
import sloth.basic.qos.QoSObserver;
import sloth.basic.qos.RouteStats;

@RequestMapping(path = "stats")
public class HTTPQoSObserver extends QoSObserver<HTTPRequest, HTTPResponse> {

    @Override
    protected void register() {

    }
    @Override
    public QoSData<HTTPRequest, HTTPResponse> newEvent() {
        return new HTTPQoSData();
    }

    @MethodMapping(method = MethodHTTP.GET)
    public RouteStats getStats(@Param(name = "route") String route) {
        return stats.getOrDefault(route, new RouteStats());
    }
}
