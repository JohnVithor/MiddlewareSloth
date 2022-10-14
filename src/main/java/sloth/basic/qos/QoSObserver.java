package sloth.basic.qos;

import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.marshaller.Sizeable;

import java.util.concurrent.ConcurrentHashMap;

public abstract class QoSObserver<Request extends Sizeable, Response extends Sizeable> {

    protected final ConcurrentHashMap<String, RouteStats<Request, Response>> stats = new ConcurrentHashMap<>();

    protected abstract void register();

    public abstract QoSData<Request, Response> newEvent();

    public void endEvent(QoSData<Request, Response> data) {
        data.endEvent();
        stats.compute(data.getId(),
                (id, routeStats) -> routeStats == null
                        ? new RouteStats<Request, Response>(data)
                        : routeStats.update(data));
    };
}
