package sloth.basic.protocols;

import sloth.basic.extension.protocolplugin.Connection;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPConnection implements Connection {

    private final DatagramSocket socket;
    private final DatagramPacket packet;
    private final BufferedReader in;

    public UDPConnection(DatagramSocket socket, DatagramPacket packet) throws IOException {
        this.socket = socket;
        this.packet = packet;
        in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packet.getData())));
    }

    @Override
    public BufferedReader getInput() {
        return in;
    }

    @Override
    public void send(String message) throws IOException {
        byte[] replymsg = message.getBytes();
        packet.setData(replymsg);
        packet.setLength(replymsg.length);
        socket.send(packet);
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    @Override
    public InetAddress getInetAddress() {
        return packet.getAddress();
    }
}
