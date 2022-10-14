package sloth.basic.qos;


import sloth.basic.marshaller.Sizeable;

import java.io.Serializable;
import java.time.Duration;

public class RouteStats<Request extends Sizeable, Response extends Sizeable> implements Serializable {
    private Duration meanEventDuration;
    private Duration meanUnmarshallDuration;
    private Duration meanBeforeInvokeDuration;
    private Duration meanInvokeDuration;
    private Duration meanErrorHandleDuration;
    private Duration meanAfterInvokeDuration;
    private Duration meanMarshallDuration;
    private Duration meanWriteResponseDuration;
    private long errorCount = 0;
    private long callCount = 0;
    private long meanRequestSize = 0;
    private long meanResponseSize = 0;

    public RouteStats() {
        meanEventDuration = Duration.ZERO;
        meanUnmarshallDuration = Duration.ZERO;
        meanBeforeInvokeDuration = Duration.ZERO;
        meanInvokeDuration = Duration.ZERO;
        meanErrorHandleDuration = Duration.ZERO;
        meanAfterInvokeDuration = Duration.ZERO;
        meanMarshallDuration = Duration.ZERO;
        meanWriteResponseDuration = Duration.ZERO;
    }

    public RouteStats(QoSData<Request, Response> data) {
        meanEventDuration = data.getEventDuration();
        meanUnmarshallDuration = data.getUnmarshallDuration();
        meanBeforeInvokeDuration = data.getBeforeInvokeDuration();
        meanInvokeDuration = data.getInvokeDuration();
        meanErrorHandleDuration = data.getErrorHandleDuration();
        meanAfterInvokeDuration = data.getAfterInvokeDuration();
        meanMarshallDuration = data.getMarshallDuration();
        meanWriteResponseDuration = data.getWriteResponseDuration();
        ++callCount;
        errorCount = data.getErrors().size() > 0 ? 1 : 0;
        meanRequestSize = data.getRequest().size();
        meanResponseSize = data.getResponse().size();
    }

    RouteStats<Request, Response> update(QoSData<Request, Response> data) {
        meanEventDuration = meanEventDuration.plus(data.getEventDuration().minus(meanEventDuration)).dividedBy(callCount);
        meanUnmarshallDuration = meanUnmarshallDuration.plus(data.getUnmarshallDuration().minus(meanUnmarshallDuration)).dividedBy(callCount);
        meanBeforeInvokeDuration = meanBeforeInvokeDuration.plus(data.getBeforeInvokeDuration().minus(meanBeforeInvokeDuration)).dividedBy(callCount);
        meanInvokeDuration = meanInvokeDuration.plus(data.getInvokeDuration().minus(meanInvokeDuration)).dividedBy(callCount);
        meanErrorHandleDuration = meanErrorHandleDuration.plus(data.getErrorHandleDuration().minus(meanErrorHandleDuration)).dividedBy(callCount);
        meanAfterInvokeDuration = meanAfterInvokeDuration.plus(data.getAfterInvokeDuration().minus(meanAfterInvokeDuration)).dividedBy(callCount);
        meanMarshallDuration = meanMarshallDuration.plus(data.getMarshallDuration().minus(meanMarshallDuration)).dividedBy(callCount);
        meanWriteResponseDuration = meanWriteResponseDuration.plus(data.getWriteResponseDuration().minus(meanWriteResponseDuration)).dividedBy(callCount);
        meanRequestSize = meanRequestSize + ((data.getRequest().size()- meanRequestSize) / callCount);
        meanResponseSize = meanResponseSize + ((data.getResponse().size() - meanResponseSize) / callCount);
        ++callCount;
        errorCount = data.getErrors().size() > 0 ? errorCount+1 : errorCount;
        return this;
    }

    public Duration getMeanEventDuration() {
        return meanEventDuration;
    }

    public Duration getMeanUnmarshallDuration() {
        return meanUnmarshallDuration;
    }

    public Duration getMeanBeforeInvokeDuration() {
        return meanBeforeInvokeDuration;
    }

    public Duration getMeanInvokeDuration() {
        return meanInvokeDuration;
    }

    public Duration getMeanErrorHandleDuration() {
        return meanErrorHandleDuration;
    }

    public Duration getMeanAfterInvokeDuration() {
        return meanAfterInvokeDuration;
    }

    public Duration getMeanMarshallDuration() {
        return meanMarshallDuration;
    }

    public Duration getMeanWriteResponseDuration() {
        return meanWriteResponseDuration;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public long getCallCount() {
        return callCount;
    }

    public long getMeanRequestSize() {
        return meanRequestSize;
    }

    public long getMeanResponseSize() {
        return meanResponseSize;
    }

    @Override
    public String toString() {
        return "RouteStats{" +
                "meanEventDuration=" + meanEventDuration +
                ", meanUnmarshallDuration=" + meanUnmarshallDuration +
                ", meanBeforeInvokeDuration=" + meanBeforeInvokeDuration +
                ", meanInvokeDuration=" + meanInvokeDuration +
                ", meanErrorHandleDuration=" + meanErrorHandleDuration +
                ", meanAfterInvokeDuration=" + meanAfterInvokeDuration +
                ", meanMarshallDuration=" + meanMarshallDuration +
                ", meanWriteResponseDuration=" + meanWriteResponseDuration +
                ", errorCount=" + errorCount +
                ", callCount=" + callCount +
                ", meanRequestSize=" + meanRequestSize +
                ", meanResponseSize=" + meanResponseSize +
                '}';
    }
}
