package sloth.basic.error;

public class BadRequestException extends RemotingException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
