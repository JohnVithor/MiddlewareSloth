package sloth.basic.error.exceptions;

public class BadRequestException extends RemotingException {
    public BadRequestException(String message) {
        super(400, message);
    }
}
