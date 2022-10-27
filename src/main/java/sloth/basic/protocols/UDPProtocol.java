package sloth.basic.protocols;

import sloth.basic.extension.protocolplugin.Connection;
import sloth.basic.extension.protocolplugin.Protocol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPProtocol implements Protocol {

    private DatagramSocket socket;

    @Override
    public void init(int port) throws IOException {
        socket = new DatagramSocket(port);
    }

    @Override
    public Connection connect() throws IOException {
        byte[] receivemessage = new byte[12800];
        DatagramPacket receivepacket = new DatagramPacket(receivemessage, receivemessage.length);
        socket.receive(receivepacket);
        return new UDPConnection(socket, receivepacket);
    }
}
