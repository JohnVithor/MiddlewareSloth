package sloth.basic.http;

import java.util.HashMap;
import java.util.Map;

public class HTTPRequest {
    private final String method;
    private final String query;

    private final Map<String, String> queryParams;
    private final String version;
    private final HashMap<String, String> headers;
    private final String body;

    public HTTPRequest(String method, String query, Map<String, String> queryParams, String version, HashMap<String, String> headers, String body) {
        this.method = method;
        this.query = query;
        this.queryParams = queryParams;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
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
                "method='" + method + '\'' +
                ", query='" + query + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
