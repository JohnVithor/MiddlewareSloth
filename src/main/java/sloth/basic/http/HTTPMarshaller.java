package sloth.basic.http;

import sloth.basic.http.error.UnmarshalException;
import sloth.basic.http.data.HTTPRequest;
import sloth.basic.http.data.HTTPResponse;
import sloth.basic.http.data.MethodHTTP;
import sloth.basic.marshaller.Marshaller;
import sloth.basic.marshaller.UnmarshallResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HTTPMarshaller implements Marshaller<HTTPRequest, HTTPResponse> {

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

    public UnmarshallResult<HTTPRequest> unmarshall(BufferedReader in, InetAddress address) throws IOException {
        UnmarshallResult<HTTPRequest> result = new UnmarshallResult<>();
        result.exceptionList = new ArrayList<>();
        String headerLine = in.readLine();
        StringTokenizer tokenizer = new StringTokenizer(headerLine);
        String method = tokenizer.nextToken();
        String query = tokenizer.nextToken();
        String version = tokenizer.nextToken();
        if (!version.equals("HTTP/1.1")) {
            result.exceptionList.add(new UnmarshalException("Not a valid HTTP version: " + version));
        }
        String inputLine;
        HashMap<String, String> headers = new HashMap<>();
        while (!(inputLine = in.readLine()).equals("")) {
            String[] splits = inputLine.split(": ");
            if (splits.length != 2) {
                result.exceptionList.add(new UnmarshalException("Not a valid HTTP header format: " + inputLine));
            } else {
                headers.put(splits[0], splits[1]);
            }
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
            result.exceptionList.add(new UnmarshalException("Ill-formed parameters on: " + query));
        }
        result.data = new HTTPRequest(address.getHostAddress(), MethodHTTP.valueOf(method), query, queryParams, version, headers, body.toString());
        result.success = result.exceptionList.isEmpty();
        return result;
    }

}
