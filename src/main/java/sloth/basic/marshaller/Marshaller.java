package sloth.basic.marshaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;

public interface Marshaller<Request, Response> {
    String marshall(Response response);
    Request unmarshall(BufferedReader in, InetAddress address) throws IOException, UnmarshalException;
}
