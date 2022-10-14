package sloth.basic.qos;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public abstract class QoSData<Request, Response> {

    private Request request;
    private Response response;
    private final Instant eventStart;
    private Instant eventEnd;
    private Instant unmarshallStart;
    private Instant unmarshallEnd;
    private Instant beforeInvokeStart;
    private Instant beforeInvokeEnd;
    private Instant invokeStart;
    private Instant invokeEnd;
    private Instant errorHandleStart;
    private Instant errorHandleEnd;
    private Instant afterInvokeStart;
    private Instant afterInvokeEnd;
    private Instant marshallStart;
    private Instant marshallEnd;
    private Instant writeResponseStart;
    private Instant writeResponseEnd;

    private final List<Exception> errors = new ArrayList<>();
    //

    public QoSData() {
        eventStart = Instant.now();
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void unmarshallStart() {
        unmarshallStart = Instant.now();
    }

    public void unmarshallEndAndBeforeInvokeStart() {
        unmarshallEnd = Instant.now();
        beforeInvokeStart = unmarshallEnd;
    }

    public void beforeInvokeEndAndInvokeStart() {
        beforeInvokeEnd = Instant.now();
        invokeStart = beforeInvokeEnd;
    }

    public void invokeEnd() {
        invokeEnd = Instant.now();
    }

    public void errorHandleStart() {
        invokeEnd = Instant.now();
        errorHandleStart = Instant.now();
    }

    public void addError(Exception e) {
        errors.add(e);
    }

    public List<Exception> getErrors() {
        return errors;
    }

    public void errorHandleEnd() {
        errorHandleEnd = Instant.now();
    }

    public void afterInvokeStart() {
        afterInvokeStart = Instant.now();
    }

    public void afterInvokeEndAndMarshallStart() {
        afterInvokeEnd = Instant.now();
        marshallStart = afterInvokeEnd;
    }

    public void marshallEndAndWriteResponseStart() {
        marshallEnd = Instant.now();
        writeResponseStart = marshallEnd;
    }

    public void writeResponseEnd() {
        writeResponseEnd = Instant.now();
    }

    public void endEvent() {
        eventEnd = Instant.now();
    }

    abstract public String getId();

    public Instant getUnmarshallStart() {
        return unmarshallStart;
    }

    public Instant getUnmarshallEnd() {
        return unmarshallEnd;
    }

    public Instant getBeforeInvokeStart() {
        return beforeInvokeStart;
    }

    public Instant getBeforeInvokeEnd() {
        return beforeInvokeEnd;
    }

    public Instant getInvokeStart() {
        return invokeStart;
    }

    public Instant getInvokeEnd() {
        return invokeEnd;
    }

    public Instant getErrorHandleStart() {
        return errorHandleStart;
    }

    public Instant getErrorHandleEnd() {
        return errorHandleEnd;
    }

    public Instant getAfterInvokeStart() {
        return afterInvokeStart;
    }

    public Instant getAfterInvokeEnd() {
        return afterInvokeEnd;
    }

    public Instant getMarshallStart() {
        return marshallStart;
    }

    public Instant getMarshallEnd() {
        return marshallEnd;
    }

    public Instant getWriteResponseStart() {
        return writeResponseStart;
    }

    public Instant getWriteResponseEnd() {
        return writeResponseEnd;
    }

    public Instant getEventStart() {
        return eventStart;
    }

    public Instant getEventEnd() {
        return eventEnd;
    }


    Duration getUnmarshallDuration() {
        return Duration.between(getUnmarshallStart(), getUnmarshallEnd());
    }

    Duration getBeforeInvokeDuration() {
        return Duration.between(getBeforeInvokeStart(), getBeforeInvokeEnd());
    }

    Duration getInvokeDuration() {
        return Duration.between(getInvokeStart(), getInvokeEnd());
    }

    Duration getErrorHandleDuration(){
        if(getErrorHandleStart() == null || getErrorHandleEnd() == null) {
            return Duration.ZERO;
        }
        return Duration.between(getErrorHandleStart(), getErrorHandleEnd());
    }

    Duration getAfterInvokeDuration(){
        return Duration.between(getAfterInvokeStart(), getAfterInvokeEnd());
    }

    Duration getMarshallDuration(){
        return Duration.between(getMarshallStart(), getMarshallEnd());
    }

    Duration getWriteResponseDuration(){
        return Duration.between(getWriteResponseStart(), getWriteResponseEnd());
    }

    Duration getEventDuration() {
        return Duration.between(getEventStart(), getEventEnd());
    }

    @Override
    public String toString() {
        return "QoSData{" +
                "request=" + request +
                ", response=" + response +
                ", eventStart=" + eventStart +
                ", eventEnd=" + eventEnd +
                ", unmarshallStart=" + unmarshallStart +
                ", unmarshallEnd=" + unmarshallEnd +
                ", beforeInvokeStart=" + beforeInvokeStart +
                ", beforeInvokeEnd=" + beforeInvokeEnd +
                ", invokeStart=" + invokeStart +
                ", invokeEnd=" + invokeEnd +
                ", errorHandleStart=" + errorHandleStart +
                ", errorHandleEnd=" + errorHandleEnd +
                ", afterInvokeStart=" + afterInvokeStart +
                ", afterInvokeEnd=" + afterInvokeEnd +
                ", marshallStart=" + marshallStart +
                ", marshallEnd=" + marshallEnd +
                ", writeResponseStart=" + writeResponseStart +
                ", writeResponseEnd=" + writeResponseEnd +
                ", errors=" + errors +
                '}';
    }
}

