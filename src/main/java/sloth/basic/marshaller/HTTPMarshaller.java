package sloth.basic.marshaller;

import sloth.basic.error.exceptions.UnmarshalException;
import sloth.basic.http.HTTPRequest;
import sloth.basic.http.HTTPResponse;
import sloth.basic.http.MethodHTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HTTPMarshaller implements Marshaller<HTTPRequest, HTTPResponse>{

    public String marshall(HTTPResponse response) {
        StringBuilder httpResponse = new StringBuilder();
        httpResponse.append("HTTP/1.1 ");
        httpResponse.append(response.getStatusCode());
        httpResponse.append(" ");
        httpResponse.append(response.getStatusMessage());
        httpResponse.append("\r\n");
        for (Map.Entry<String, String> entry: response.getHeaders().entrySet()) {
            httpResponse.append(entry.getKey());
            httpResponse.append(": ");
            httpResponse.append(entry.getValue());
            httpResponse.append("\r\n");
        }
        httpResponse.append("\r\n");
        httpResponse.append(response.getBody());
        return httpResponse.toString();
    }

    public HTTPRequest unmarshall(BufferedReader in, InetAddress address) throws IOException, UnmarshalException {
        String headerLine = in.readLine();
        StringTokenizer tokenizer = new StringTokenizer(headerLine);
        String method = tokenizer.nextToken();
        String query = tokenizer.nextToken();
        String version = tokenizer.nextToken();
        if (!version.equals("HTTP/1.1")) {
            throw new UnmarshalException("Not a valid HTTP version: " + version);
        }
        String inputLine;
        HashMap<String, String> headers = new HashMap<>();
        while (!(inputLine = in.readLine()).equals("")) {
            String[] splits = inputLine.split(": ");
            if (splits.length != 2) {
                throw new UnmarshalException("Not a valid HTTP header format: " + inputLine);
            }
            headers.put(splits[0], splits[1]);
        }
        StringBuilder body = new StringBuilder();
        while(in.ready()){
            body.append((char) in.read());
        }

        HashMap<String, String> queryParams = new HashMap<>();
        try {
            if (query.contains("?")){
                String paramsQ = query.substring(query.indexOf("?")+1);
                for (String key_value: paramsQ.split("&")) {
                    String[] pair = key_value.split("=");
                    queryParams.put(pair[0], pair[1]);
                }
                query = query.substring(0, query.indexOf("?"));
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new UnmarshalException("Ill-formed parameters on: " + query);
        }

        return new HTTPRequest(address.getHostAddress(), MethodHTTP.valueOf(method), query, queryParams, version, headers, body.toString());
    }

}
