package sloth.basic.error;

public class RemotingException extends Exception {
    private final int status;
    public RemotingException(int status, String message) {
        super(message);
        this.status = status;
    }
    public int getStatus() {
        return status;
    }
}
