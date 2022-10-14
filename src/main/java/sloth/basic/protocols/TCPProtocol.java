package sloth.basic.protocols;

import sloth.basic.extension.protocolplugin.Connection;
import sloth.basic.extension.protocolplugin.Protocol;

import java.io.IOException;
import java.net.ServerSocket;

public class TCPProtocol implements Protocol {

    private ServerSocket socket;

    @Override
    public void init(int port) throws IOException {
        socket = new ServerSocket(port);
    }

    @Override
    public Connection connect() throws IOException {
        return new TCPConnection(socket.accept());
    }
}
