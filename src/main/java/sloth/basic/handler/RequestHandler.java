package sloth.basic.handler;

import sloth.basic.error.*;
import sloth.basic.error.RemotingException;
import sloth.basic.extension.protocolplugin.Connection;
import sloth.basic.invoker.Invoker;
import sloth.basic.marshaller.Marshaller;
import sloth.basic.marshaller.Sizeable;
import sloth.basic.qos.QoSData;
import sloth.basic.qos.QoSObserver;

import java.io.*;
import java.net.Socket;

public class RequestHandler<Request extends Sizeable, Response extends Sizeable> implements Runnable {

    private final Connection connection;
    private final Marshaller<Request, Response> marshaller;
    private final Invoker<Request, Response> invoker;
    private final ErrorHandler<Response> errorHandler;
    private final QoSObserver<Request, Response> qoSObserver;

    public RequestHandler(Connection connection,
                          Marshaller<Request, Response> marshaller,
                          Invoker<Request, Response> invoker,
                          ErrorHandler<Response> errorHandler,
                          QoSObserver<Request, Response> qoSObserver) {
        this.connection = connection;
        this.invoker = invoker;
        this.marshaller = marshaller;
        this.errorHandler = errorHandler;
        this.qoSObserver = qoSObserver;
    }

    @Override
    public void run() {
        QoSData<Request, Response> qoSData = qoSObserver.newEvent();
        try {
            Response response;
            Request request = null;
            try {
                qoSData.unmarshallStart();
                request = marshaller.unmarshall(connection.getInput(), connection.getInetAddress());
                qoSData.setRequest(request);
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
            qoSData.setResponse(response);
            qoSData.marshallEndAndWriteResponseStart();
            connection.send(responseString);
            qoSData.writeResponseEnd();
        } catch (Exception e) {
            qoSData.addError(e);
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        qoSObserver.endEvent(qoSData);
    }
}
