package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;

/**
 * Created by HyperWorks on 2017-06-16.
 */
public class StringWeatherEvent extends Event {
    private final LocalDateTime timestamp;
    private final String value;

    public StringWeatherEvent(final LocalDateTime timestamp, final String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getValue() {
        return this.value;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RawRateEvent(timestamp=" + this.getTimestamp() + ", value=" + this.getValue() + ")";
    }

}
