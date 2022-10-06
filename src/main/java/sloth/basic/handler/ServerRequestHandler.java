package sloth.basic.handler;

import sloth.basic.error.ErrorHandler;
import sloth.basic.invoker.Invoker;
import sloth.basic.marshaller.Marshaller;
import sloth.basic.qos.QoSObserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRequestHandler<Request, Response> {

    public void init(int port,
                     Marshaller<Request, Response> marshaller,
                     Invoker<Request, Response> invoker,
                     ErrorHandler<Response> errorHandler,
                     QoSObserver qoSObserver) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.err.println("Iniciando Sloth Server na porta " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread.ofVirtual().
                        start(new RequestHandler<>(socket, marshaller, invoker, errorHandler, qoSObserver));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
