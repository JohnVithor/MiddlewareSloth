package sloth.basic.handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerRequestHandler {

    public static void init(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.err.println("Iniciando Sloth Server na porta " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                Thread.ofVirtual().start(new RequestHandler(socket, null));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
