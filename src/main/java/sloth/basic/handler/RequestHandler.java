package sloth.basic.handler;

import sloth.basic.error.*;
import sloth.basic.error.exceptions.RemotingException;
import sloth.basic.invoker.Invoker;
import sloth.basic.marshaller.Marshaller;

import java.io.*;
import java.net.Socket;

public class RequestHandler<Request, Response> implements Runnable {

    private final Socket socket;
    private final Marshaller<Request, Response> marshaller;
    private final Invoker<Request, Response> invoker;
    private final ErrorHandler<Response> errorHandler;

    public RequestHandler(Socket socket,
                          Marshaller<Request, Response> marshaller,
                          Invoker<Request, Response> invoker,
                          ErrorHandler<Response> errorHandler) {
        this.socket = socket;
        this.invoker = invoker;
        this.marshaller = marshaller;
        this.errorHandler = errorHandler;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            Response response;
            Request request = null;
            try {
                request = marshaller.unmarshall(in, socket.getInetAddress());
                invoker.beforeInvoke(request);
                response = invoker.invoke(request);
            } catch (RemotingException e) {
                response = errorHandler.build(e);
            } catch (Exception e) {
                e.printStackTrace();
                response = errorHandler.build(
                        new RemotingException(errorHandler.getDefaultErrorCode(), e.getMessage())
                );
            }
            invoker.afterInvoke(request, response);
            String responseString = marshaller.marshall(response);
            out.write(responseString);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
