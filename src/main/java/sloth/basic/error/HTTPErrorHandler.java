package sloth.basic.error;

import sloth.basic.error.exceptions.RemotingException;
import sloth.basic.http.HTTPResponse;

public class HTTPErrorHandler implements ErrorHandler<HTTPResponse>{

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
