package sloth.basic.error.exceptions;

public class NotFoundException extends RemotingException {
    public NotFoundException(String message) {
        super(404, message);
    }
}
