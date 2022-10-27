package sloth.basic.extension.logging;

import sloth.basic.error.RemotingException;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.qos.RouteStats;

import java.io.*;
import java.util.Map;

public class HTTPRequestResponseLogger extends Logger<HTTPRequest, HTTPResponse> {

    private final BufferedWriter writer;

    public HTTPRequestResponseLogger(String filename) throws IOException {
        File yourFile = new File(filename);
        if(yourFile.createNewFile()) {
            System.err.println("Logging File created");
        } else {
            System.err.println("Logging File already exists");
        }
        FileOutputStream oFile = new FileOutputStream(yourFile, true);
        this.writer = new BufferedWriter(new OutputStreamWriter(oFile));
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
                    .append(httpRequest.requestor())
                    .append(" using ")
                    .append(httpRequest.method())
                    .append(" to ")
                    .append(httpRequest.query())
                    .append("\n");
            if(!httpRequest.queryParams().isEmpty()){
                builder.append("With Params:\n");
                for (Map.Entry<String, String> e: httpRequest.queryParams().entrySet()) {
                    builder.append(e.getKey())
                            .append(" : ")
                            .append(e.getValue())
                            .append("\n");
                }
            }
            if(httpRequest.body() != null && !httpRequest.body().isEmpty()){
                builder.append("With Body:\n")
                        .append(httpRequest.body());
            }
            builder.append("Had Response: ")
                    .append(httpResponse.getStatusCode())
                    .append(" ")
                    .append(httpResponse.getStatusMessage())
                    .append("\n");
            if(httpResponse.getBody() != null && !httpResponse.getBody().isEmpty()){
                builder.append("With Body:\n")
                        .append(httpResponse.getBody())
                        .append("\n");
            }
            queue.put(builder.append("\n").toString());
        } catch (InterruptedException e) {
            System.err.println("Não foi possivel realizar o logging devido a: " + e.getMessage());
        }
    }

    @Override
    public void write(String message) throws IOException {
        writer.write(message);
        writer.flush();
    }
}
