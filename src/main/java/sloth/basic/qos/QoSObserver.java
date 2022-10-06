package sloth.basic.qos;

public interface QoSObserver<Request, Response> {

    void register();

    QoSData<Request, Response> newEvent();

    void endEvent(QoSData<Request, Response> data);
}
