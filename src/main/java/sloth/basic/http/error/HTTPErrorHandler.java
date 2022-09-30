package sloth.basic.http.error;

import sloth.basic.error.ErrorHandler;
import sloth.basic.error.RemotingException;
import sloth.basic.http.data.HTTPResponse;

public class HTTPErrorHandler implements ErrorHandler<HTTPResponse> {

    public HTTPResponse build(RemotingException e) {
        return new HTTPResponse(
                "HTTP/1.1",
                e.getStatus(),
                HTTPResponse.getMessage(e.getStatus()),
                HTTPResponse.buildBasicHeaders(e.getMessage()),
                e.getMessage()
        );
    }

    @Override
    public int getDefaultErrorCode() {
        return 500;
    }
}
