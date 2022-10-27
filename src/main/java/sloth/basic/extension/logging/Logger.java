package sloth.basic.extension.logging;

import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.marshaller.IdentifiedSizeable;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Logger<Request extends IdentifiedSizeable, Response extends IdentifiedSizeable> implements InvocationInterceptor<Request, Response> {

    protected final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);

    public void init() {
        Thread.ofVirtual().start(() -> {
            try {
                while (true) {
                    this.write(queue.take());
                }
            } catch (InterruptedException | IOException e) {
                System.err.println("Logger interrompido: " + e.getMessage());
            }
        });
    }

    public abstract void write(String message) throws IOException;
}
