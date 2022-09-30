package sloth.basic.http.error;

import sloth.basic.error.RemotingException;

public class BadRequestException extends RemotingException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
