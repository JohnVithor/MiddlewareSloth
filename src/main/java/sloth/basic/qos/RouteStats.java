package sloth.basic.qos;

import java.time.Duration;

public class RouteStats {
    private Duration eventDuration;
    private Duration unmarshallDuration;
    private Duration beforeInvokeDuration;
    private Duration invokeDuration;
    private Duration errorHandleDuration;
    private Duration afterInvokeDuration;
    private Duration marshallDuration;
    private Duration writeResponseDuration;
    private long errorCount = 0;
    private long callCount = 0;

    public RouteStats(QoSData<?,?> data) {
        eventDuration = data.getEventDuration();
        unmarshallDuration = data.getUnmarshallDuration();
        beforeInvokeDuration = data.getBeforeInvokeDuration();
        invokeDuration = data.getInvokeDuration();
        errorHandleDuration = data.getErrorHandleDuration();
        afterInvokeDuration = data.getAfterInvokeDuration();
        marshallDuration = data.getMarshallDuration();
        writeResponseDuration = data.getWriteResponseDuration();
        ++callCount;
        errorCount = data.getErrors().size() > 0 ? 1 : 0;
    }

    RouteStats update(QoSData<?,?> data) {
        eventDuration = eventDuration.plus(data.getEventDuration().minus(eventDuration)).dividedBy(callCount);
        unmarshallDuration = unmarshallDuration.plus(data.getUnmarshallDuration().minus(unmarshallDuration)).dividedBy(callCount);
        beforeInvokeDuration = beforeInvokeDuration.plus(data.getBeforeInvokeDuration().minus(beforeInvokeDuration)).dividedBy(callCount);
        invokeDuration = invokeDuration.plus(data.getInvokeDuration().minus(invokeDuration)).dividedBy(callCount);
        errorHandleDuration = errorHandleDuration.plus(data.getErrorHandleDuration().minus(errorHandleDuration)).dividedBy(callCount);
        afterInvokeDuration = afterInvokeDuration.plus(data.getAfterInvokeDuration().minus(afterInvokeDuration)).dividedBy(callCount);
        marshallDuration = marshallDuration.plus(data.getMarshallDuration().minus(marshallDuration)).dividedBy(callCount);
        writeResponseDuration = writeResponseDuration.plus(data.getWriteResponseDuration().minus(writeResponseDuration)).dividedBy(callCount);
        ++callCount;
        errorCount = data.getErrors().size() > 0 ? errorCount+1 : errorCount;
        return this;
    }
}
