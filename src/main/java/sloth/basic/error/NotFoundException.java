package sloth.basic.error;

public class NotFoundException extends RemotingException {
    public NotFoundException(String message) {
        super(message);
    }
}
