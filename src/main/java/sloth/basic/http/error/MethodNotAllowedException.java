package sloth.basic.http.error;

import sloth.basic.error.RemotingException;

public class MethodNotAllowedException extends RemotingException {
    public MethodNotAllowedException(String message) {
        super(405, message);
    }
}
