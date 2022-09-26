package sloth.basic.invoker;


import sloth.basic.error.RemotingException;

public interface Invoker<Request, Response> {

    Response invoke(Request request) throws RemotingException;

    void register(Object object);
}
