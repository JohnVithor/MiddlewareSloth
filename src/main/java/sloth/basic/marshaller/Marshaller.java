package sloth.basic.marshaller;

import java.io.BufferedReader;
import java.io.IOException;

public interface Marshaller<Request, Response> {
    String marshall(Response response);
    Request marshall(BufferedReader in) throws IOException, UnmarshalException;
}
