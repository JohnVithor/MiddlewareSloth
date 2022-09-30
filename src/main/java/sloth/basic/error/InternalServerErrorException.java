package sloth.basic.error;

public class InternalServerErrorException extends RemotingException {
    public InternalServerErrorException(String message) {
        super(500, message);
    }
}
