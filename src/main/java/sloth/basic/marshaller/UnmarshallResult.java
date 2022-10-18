package sloth.basic.marshaller;

import sloth.basic.http.error.UnmarshalException;

import java.util.List;

public class UnmarshallResult<Request extends IdentifiedSizeable> {
    public Request data;
    public List<UnmarshalException> exceptionList;
    public String getMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        for (UnmarshalException e: exceptionList) {
            stringBuilder.append(e.getMessage());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
    public boolean success;

}
