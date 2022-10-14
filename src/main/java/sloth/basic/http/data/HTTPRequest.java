package sloth.basic.http.data;

import sloth.basic.marshaller.Sizeable;

import java.util.HashMap;
import java.util.Map;

public class HTTPRequest implements Sizeable {
    private final String requestor;
    private final MethodHTTP method;
    private final String query;
    private final Map<String, String> queryParams;
    private final String version;
    private final HashMap<String, String> headers;
    private final String body;

    public HTTPRequest(String requestor, MethodHTTP method, String query, Map<String, String> queryParams, String version, HashMap<String, String> headers, String body) {
        this.requestor = requestor;
        this.method = method;
        this.query = query;
        this.queryParams = queryParams;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public String getRequestor() {
        return requestor;
    }

    public MethodHTTP getMethod() {
        return method;
    }

    public String getQuery() {
        return query;
    }

    public String getVersion() {
        return version;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

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
    public long size() {
        return body.length();
    }
}
