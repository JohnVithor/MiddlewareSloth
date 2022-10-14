package sloth.basic.extension.protocolplugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;

public interface Connection {
    BufferedReader getInput() throws IOException;
    void send(String message) throws IOException;
    void close() throws IOException;

    InetAddress getInetAddress();
}
