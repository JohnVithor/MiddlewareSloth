package sloth.basic.protocols;

import sloth.basic.extension.protocolplugin.Connection;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPConnection implements Connection {

    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public TCPConnection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    @Override
    public BufferedReader getInput() {
        return in;
    }

    @Override
    public void send(String message) throws IOException {
        out.write(message);
        out.flush();
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    @Override
    public InetAddress getInetAddress() {
        return socket.getInetAddress();
    }
}
