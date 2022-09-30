package sloth.basic.http.error;

import sloth.basic.error.RemotingException;

public class InternalServerErrorException extends RemotingException {
    public InternalServerErrorException(String message) {
        super(500, message);
    }
}
