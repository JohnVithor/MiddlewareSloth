package sloth.basic.error.exceptions;

public class MethodNotAllowedException extends RemotingException {
    public MethodNotAllowedException(String message) {
        super(405, message);
    }
}
