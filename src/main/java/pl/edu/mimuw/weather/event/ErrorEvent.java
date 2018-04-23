package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;

/**
 * Created by HyperWorks on 2017-06-13.
 */
public final class ErrorEvent extends Event {
    private final LocalDateTime timestamp;
    private final Throwable cause;

    public ErrorEvent(Throwable cause) {
        this.timestamp = LocalDateTime.now();
        this.cause = cause;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public Throwable getCause() {
        return this.cause;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ErrorEvent(timestamp=" + this.getTimestamp() + ", cause=" + this.getCause() + ")";
    }

}
