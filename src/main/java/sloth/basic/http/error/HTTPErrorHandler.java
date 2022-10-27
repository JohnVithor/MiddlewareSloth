package sloth.basic.http.error;

import sloth.basic.error.ErrorHandler;
import sloth.basic.error.RemotingException;
import sloth.basic.http.data.HTTPResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

public class HTTPErrorHandler implements ErrorHandler<HTTPResponse> {

    public HTTPResponse build(RemotingException e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return new HTTPResponse(
                "HTTP/1.1",
                e.getStatus(),
                HTTPResponse.getMessage(e.getStatus()),
                HTTPResponse.buildBasicHeaders(e.getMessage()==null?sw.toString():e.getMessage()),
                e.getMessage()
        );
    }

    @Override
    public int getDefaultErrorCode() {
        return 500;
    }
}
