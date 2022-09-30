package sloth.basic.error.exceptions;

public class UnmarshalException extends RemotingException {

    public UnmarshalException(String message) {
        super(400, message);
    }
}
