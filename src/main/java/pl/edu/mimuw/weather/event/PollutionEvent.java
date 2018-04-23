package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public final class PollutionEvent extends Event {
    private final float PM2_5;
    private final float PM10;
    private final LocalDateTime timestamp;

    public PollutionEvent(final float PM2_5, final float PM10) {
        timestamp = LocalDateTime.now();
        this.PM2_5=PM2_5;
        this.PM10=PM10;
    }

    public float getPM2_5() {
        return PM2_5;
    }

    public float getPM10() {
        return PM10;
    }

    @Override
    public String toString() {
        return "PollutionEvent{" +
                "PM2_5=" + PM2_5 +
                ", PM10=" + PM10 +
                ", timestamp=" + timestamp +
                '}';
    }
}
