package sloth.basic.http;

import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.http.qos.HTTPQoSData;
import sloth.basic.qos.QoSData;
import sloth.basic.qos.QoSObserver;

public class HTTPQoSObserver extends QoSObserver<HTTPRequest, HTTPResponse> {


    @Override
    protected void register() {

    }

    @Override
    public QoSData<HTTPRequest, HTTPResponse> newEvent() {
        return new HTTPQoSData();
    }

    @Override
    public void endEvent(QoSData<HTTPRequest, HTTPResponse> data) {

    }
}
