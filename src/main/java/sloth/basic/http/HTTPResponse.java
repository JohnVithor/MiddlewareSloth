package sloth.basic.http;

import sloth.basic.util.RouteInfo;

import java.util.HashMap;

public class HTTPResponse {
    private final String version;
    private final int statusCode;
    private final String statusMessage;
    private final HashMap<String, String> headers;
    private final String body;

    public HTTPResponse(String version, int statusCode, String statusMessage, HashMap<String, String> headers, String body) {
        this.version = version;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = headers;
        this.body = body;
    }

    public String getVersion() {
        return version;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HTTPResponse{" +
                "version='" + version + '\'' +
                ", statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }

    public static String getMessage(int statusCode) {
        return switch (statusCode) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            default -> "Internal Server Error";
        };
    }

    public static HashMap<String, String> buildBasicHeaders(String body) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html; charset=utf-8");
        headers.put("Content-Length", String.valueOf(body.length()));
        return headers;
    }

    public static HashMap<String, String> buildBasicHeaders(String body, String content_type) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", content_type);
        headers.put("Content-Length", String.valueOf(body.length()));
        return headers;
    }
}
