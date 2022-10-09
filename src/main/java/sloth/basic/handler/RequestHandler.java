package sloth.basic.handler;

import sloth.basic.error.*;
import sloth.basic.error.RemotingException;
import sloth.basic.invoker.Invoker;
import sloth.basic.marshaller.Marshaller;
import sloth.basic.qos.QoSData;
import sloth.basic.qos.QoSObserver;

import java.io.*;
import java.net.Socket;

public class RequestHandler<Request, Response> implements Runnable {

    private final Socket socket;
    private final Marshaller<Request, Response> marshaller;
    private final Invoker<Request, Response> invoker;
    private final ErrorHandler<Response> errorHandler;
    private final QoSObserver<Request, Response> qoSObserver;

    public RequestHandler(Socket socket,
                          Marshaller<Request, Response> marshaller,
                          Invoker<Request, Response> invoker,
                          ErrorHandler<Response> errorHandler,
                          QoSObserver<Request, Response> qoSObserver) {
        this.socket = socket;
        this.invoker = invoker;
        this.marshaller = marshaller;
        this.errorHandler = errorHandler;
        this.qoSObserver = qoSObserver;
    }

    @Override
    public void run() {
        QoSData<Request, Response> qoSData = qoSObserver.newEvent();
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            Response response;
            Request request = null;
            try {
                qoSData.unmarshallStart();
                request = marshaller.unmarshall(in, socket.getInetAddress());
                qoSData.unmarshallEndAndBeforeInvokeStart();
                invoker.beforeInvoke(request);
                qoSData.beforeInvokeEndAndInvokeStart();
                response = invoker.invoke(request);
                qoSData.invokeEnd();
            } catch (RemotingException e) {
                qoSData.errorHandleStart();
                qoSData.addError(e);
                response = errorHandler.build(e);
                qoSData.errorHandleEnd();
            } catch (Exception e) {
                qoSData.errorHandleStart();
                qoSData.addError(e);
                e.printStackTrace();
                response = errorHandler.build(
                        new RemotingException(errorHandler.getDefaultErrorCode(), e.getMessage())
                );
                qoSData.errorHandleEnd();
            }
            qoSData.afterInvokeStart();
            invoker.afterInvoke(request, response);
            qoSData.afterInvokeEndAndMarshallStart();
            String responseString = marshaller.marshall(response);
            qoSData.marshallEndAndWriteResponseStart();
            out.write(responseString);
            out.flush();
            qoSData.writeResponseEnd();
        } catch (Exception e) {
            qoSData.addError(e);
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        qoSObserver.endEvent(qoSData);
    }
}
