package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;

/**
 * Created by HyperWorks on 2017-06-07.
 */
public class Event {
    private final LocalDateTime timestamp;

    public Event() {
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "event.Event()";
    }
}
