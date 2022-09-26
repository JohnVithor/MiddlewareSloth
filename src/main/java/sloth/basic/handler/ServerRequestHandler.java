package sloth.basic.handler;

import sloth.basic.invoker.HTTPInvoker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRequestHandler {

    public static void init(int port, HTTPInvoker invoker) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.err.println("Iniciando Sloth Server na porta " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread.ofVirtual().start(new RequestHandler(socket, invoker));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
