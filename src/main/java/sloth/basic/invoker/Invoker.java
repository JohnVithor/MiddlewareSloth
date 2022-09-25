package sloth.basic.invoker;


public interface Invoker<Request, Response> {

    Response invoke(Request request);

    void register(Object object);
}
