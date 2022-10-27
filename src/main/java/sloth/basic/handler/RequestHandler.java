package sloth.basic.handler;

import sloth.basic.error.*;
import sloth.basic.extension.protocolplugin.Connection;
import sloth.basic.invoker.Invoker;
import sloth.basic.marshaller.Marshaller;
import sloth.basic.marshaller.IdentifiedSizeable;
import sloth.basic.marshaller.UnmarshallResult;
import sloth.basic.qos.QoSData;
import sloth.basic.qos.QoSObserver;

import java.io.*;

public class RequestHandler<Request extends IdentifiedSizeable, Response extends IdentifiedSizeable> implements Runnable {

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
            UnmarshallResult<Request> request = null;
            String id = "";
            try {
                qoSData.unmarshallStart();
                request = marshaller.unmarshall(connection.getInput(), connection.getInetAddress());
                id = request.data.getId();
                qoSData.setRequest(request.data);
                qoSData.unmarshallEndAndBeforeInvokeStart();
                if (!request.success) {
                    throw new RemotingException(400, request.getMessage());
                }
                invoker.beforeInvoke(request.data, qoSObserver.get(id));
                qoSData.beforeInvokeEndAndInvokeStart();
                response = invoker.invoke(request.data);
                qoSData.invokeEnd();
            } catch (RemotingException e) {
                qoSData.errorHandleStart();
                qoSData.addError(e);
                response = errorHandler.build(e);
                qoSData.errorHandleEnd();
            } catch (Exception e) {
                qoSData.errorHandleStart();
                qoSData.addError(e);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                response = errorHandler.build(
                        new RemotingException(errorHandler.getDefaultErrorCode(), e.getMessage()==null?sw.toString():e.getMessage())
                );
                qoSData.errorHandleEnd();
            }
            qoSData.afterInvokeStart();
            invoker.afterInvoke(request.data, response, qoSObserver.get(id));
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
        Thread.ofVirtual().start(() -> qoSObserver.endEvent(qoSData));
    }
}
