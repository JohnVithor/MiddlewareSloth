package sloth.basic.error;

public interface ErrorHandler<Response> {

    Response build(RemotingException e);
    int getDefaultErrorCode();
}
