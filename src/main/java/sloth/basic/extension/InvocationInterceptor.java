package sloth.basic.extension;

import sloth.basic.error.RemotingException;
import sloth.basic.marshaller.Sizeable;

public interface InvocationInterceptor<Request extends Sizeable, Response extends Sizeable> extends Comparable<InvocationInterceptor<Request, Response>>{

    int getPriority();

    void beforeRequest(Request request) throws RemotingException;

    void afterResponse(Request request, Response response) throws RemotingException;

    default int compareTo(InvocationInterceptor o) {
        return Integer.compare(getPriority(), o.getPriority());
    }
}
