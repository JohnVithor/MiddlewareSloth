package sloth.basic.qos;

import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.marshaller.IdentifiedSizeable;

import java.util.concurrent.ConcurrentHashMap;

public abstract class QoSObserver<Request extends IdentifiedSizeable, Response extends IdentifiedSizeable> {

    protected final ConcurrentHashMap<String, RouteStats<Request, Response>> stats = new ConcurrentHashMap<>();

    protected abstract void register();

    public abstract QoSData<Request, Response> newEvent();

    public void endEvent(QoSData<Request, Response> data) {
        data.endEvent();
        stats.compute(data.getId(),
                (id, routeStats) -> routeStats == null
                        ? new RouteStats<>(data)
                        : routeStats.update(data));
    }

    public RouteStats<Request, Response> get(String id) {
        return stats.getOrDefault(id, new RouteStats<>());
    }
}
