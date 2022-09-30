package sloth.basic.http.error;

import sloth.basic.error.RemotingException;

public class UnmarshalException extends RemotingException {

    public UnmarshalException(String message) {
        super(400, message);
    }
}
