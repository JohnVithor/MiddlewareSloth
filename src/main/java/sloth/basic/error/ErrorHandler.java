package sloth.basic.error;

import sloth.basic.error.exceptions.RemotingException;

public interface ErrorHandler<Response> {

    Response build(RemotingException e);
    int getDefaultErrorCode();
}
