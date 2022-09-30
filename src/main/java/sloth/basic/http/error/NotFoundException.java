package sloth.basic.http.error;

import sloth.basic.error.RemotingException;

public class NotFoundException extends RemotingException {
    public NotFoundException(String message) {
        super(404, message);
    }
}
