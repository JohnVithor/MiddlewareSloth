package sloth.basic.qos;

public class DisabledQoSObserver extends QoSObserver {

    protected void register(){}

    public QoSData newEvent() {return new DisabledQoSData();}

    public void endEvent(QoSData data) {

    }
}
