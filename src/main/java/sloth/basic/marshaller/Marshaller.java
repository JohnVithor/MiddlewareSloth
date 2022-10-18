package sloth.basic.marshaller;

import sloth.basic.http.error.UnmarshalException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;

public interface Marshaller<Request extends IdentifiedSizeable, Response extends IdentifiedSizeable> {
    String marshall(Response response);
    UnmarshallResult<Request> unmarshall(BufferedReader in, InetAddress address) throws IOException;
}
