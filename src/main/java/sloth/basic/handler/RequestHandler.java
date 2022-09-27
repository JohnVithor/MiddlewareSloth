package sloth.basic.handler;

import sloth.basic.error.HTTPErrorResponseBuilder;
import sloth.basic.error.RemotingException;
import sloth.basic.http.HTTPRequest;
import sloth.basic.http.HTTPResponse;
import sloth.basic.invoker.HTTPInvoker;
import sloth.basic.invoker.Invoker;
import sloth.basic.marshaller.HTTPMarshaller;
import sloth.basic.marshaller.UnmarshalException;

import java.io.*;
import java.net.Socket;

public class RequestHandler implements Runnable {

    private final Socket socket;
    private final HTTPInvoker invoker;

    public RequestHandler(Socket socket, HTTPInvoker invoker) {
        this.socket = socket;
        this.invoker = invoker;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            try {
                HTTPRequest request = HTTPMarshaller.unmarshall(in);
                HTTPResponse response = invoker.invoke(request);
                String responseString = HTTPMarshaller.marshall(response);
                out.write(responseString);
            } catch (RemotingException e) {
                HTTPResponse response = HTTPErrorResponseBuilder.build(400, e);
                String responseString = HTTPMarshaller.marshall(response);
                out.write(responseString);
            } catch (Exception e) {
                HTTPResponse response = HTTPErrorResponseBuilder.build(500, e);
                String responseString = HTTPMarshaller.marshall(response);
                out.write(responseString);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
