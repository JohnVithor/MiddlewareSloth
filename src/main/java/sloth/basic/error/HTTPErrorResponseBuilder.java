package sloth.basic.error;

import sloth.basic.http.HTTPResponse;

public class HTTPErrorResponseBuilder {

    public static HTTPResponse build(int status, String message) {
        return new HTTPResponse(
                "HTTP/1.1",
                status,
                HTTPResponse.getMessage(status),
                HTTPResponse.buildBasicHeaders(message),
                message
        );
    }
}
