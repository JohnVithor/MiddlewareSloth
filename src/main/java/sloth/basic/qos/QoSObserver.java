package sloth.basic.qos;

import java.util.concurrent.ConcurrentHashMap;

public abstract class QoSObserver<Request, Response> {

    private final ConcurrentHashMap<String, RouteStats> stats = new ConcurrentHashMap<>();

    protected abstract void register();

    public abstract QoSData<Request, Response> newEvent();

    public void endEvent(QoSData<Request, Response> data) {
        stats.compute(data.getId(),
                (id, routeStats) -> routeStats == null ? new RouteStats(data) : routeStats.update(data));
    };
}
