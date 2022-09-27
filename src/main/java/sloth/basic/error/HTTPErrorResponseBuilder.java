package sloth.basic.error;

import sloth.basic.http.HTTPResponse;

public class HTTPErrorResponseBuilder {

    public static HTTPResponse build(int status, Exception e) {
        return new HTTPResponse(
                "HTTP/1.1",
                status,
                HTTPResponse.getMessage(status),
                HTTPResponse.buildBasicHeaders(e.getMessage()),
                e.getMessage()
                );
    }
}
