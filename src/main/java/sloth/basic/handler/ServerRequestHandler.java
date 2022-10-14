package sloth.basic.handler;

import sloth.basic.error.ErrorHandler;
import sloth.basic.extension.protocolplugin.Connection;
import sloth.basic.extension.protocolplugin.Protocol;
import sloth.basic.invoker.Invoker;
import sloth.basic.marshaller.Marshaller;
import sloth.basic.marshaller.Sizeable;
import sloth.basic.qos.QoSObserver;

import java.io.IOException;

public class ServerRequestHandler<Request extends Sizeable, Response extends Sizeable> {

    public void init(int port,
                     Protocol protocol,
                     Marshaller<Request, Response> marshaller,
                     Invoker<Request, Response> invoker,
                     ErrorHandler<Response> errorHandler,
                     QoSObserver<Request, Response> qoSObserver) {

        try {
            protocol.init(port);
            System.err.println("Iniciando Sloth Server na porta " + port);
            while (true) {
                Connection connection = protocol.connect();
                Thread.ofVirtual().start(new RequestHandler<>(
                        connection, marshaller, invoker, errorHandler, qoSObserver)
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
