package sloth.basic.extension.logging;

import sloth.basic.error.RemotingException;
import sloth.basic.extension.InvocationInterceptor;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.qos.RouteStats;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class HTTPRequestResponseLogger implements InvocationInterceptor<HTTPRequest, HTTPResponse> {

    private final BufferedWriter writer;

    private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);

    public HTTPRequestResponseLogger(BufferedWriter writer) {
        this.writer = writer;
    }

    public void init() {
        Thread.ofVirtual().start(() -> {
            while (true) {
                try {
                    String message = queue.take();
                    writer.write(message);
                    writer.flush();
                } catch (InterruptedException | IOException e) {
                    System.err.println("Logger interrompido: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public int getPriority() {
        return 10;
    }

    @Override
    public void beforeRequest(HTTPRequest httpRequest, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) throws RemotingException {
        // EMPTY //
    }

    @Override
    public void afterResponse(HTTPRequest httpRequest, HTTPResponse httpResponse, RouteStats<HTTPRequest, HTTPResponse> qoSObserver) throws RemotingException {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("Request from: ")
                    .append(httpRequest.getRequestor())
                    .append(" using ")
                    .append(httpRequest.getMethod())
                    .append(" to ")
                    .append(httpRequest.getQuery())
                    .append("\n");
            if(!httpRequest.getQueryParams().isEmpty()){
                builder.append("With Params:\n");
                for (Map.Entry<String, String> e: httpRequest.getQueryParams().entrySet()) {
                    builder.append(e.getKey())
                            .append(" : ")
                            .append(e.getValue())
                            .append("\n");
                }
            }
            if(!httpRequest.getBody().isEmpty()){
                builder.append("With Body:\n")
                        .append(httpRequest.getBody());
            }
            builder.append("Had Response: ")
                    .append(httpResponse.getStatusCode())
                    .append(" ")
                    .append(httpResponse.getStatusMessage())
                    .append("\n");
            if(!httpResponse.getBody().isEmpty()){
                builder.append("With Body:\n")
                        .append(httpResponse.getBody())
                        .append("\n");
            }
            queue.put(builder.append("\n").toString());
        } catch (InterruptedException e) {
            System.err.println("Não foi possivel realizar o logging devido a: " + e.getMessage());
        }
    }
}
