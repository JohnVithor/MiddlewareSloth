package sloth.basic.http.data;

import sloth.basic.marshaller.IdentifiedSizeable;

import java.util.HashMap;
import java.util.Map;

public record HTTPRequest(String requestor, MethodHTTP method, String query, Map<String, String> queryParams,
                          String version, HashMap<String, String> headers, String body) implements IdentifiedSizeable {

    @Override
    public String toString() {
        return "HTTPRequest{" +
                "requestor='" + requestor + '\'' +
                ", method=" + method +
                ", query='" + query + '\'' +
                ", queryParams=" + queryParams +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public long getSize() {
        return body == null?0:body.length();
    }

    @Override
    public String getId() {
        return query;
    }
}
