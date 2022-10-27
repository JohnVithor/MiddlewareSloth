package sloth.basic.http.qos;

import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.qos.QoSData;

public class HTTPQoSData extends QoSData<HTTPRequest, HTTPResponse> {

    @Override
    public String getId() {
        return getRequest().query();
    }
}
