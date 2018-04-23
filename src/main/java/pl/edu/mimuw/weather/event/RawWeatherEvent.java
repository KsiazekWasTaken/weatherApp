package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;

/**
 * Created by HyperWorks on 2017-06-15.
 */
public class RawWeatherEvent extends Event {
        private final LocalDateTime timestamp;
        private final float value;

        public RawWeatherEvent(final LocalDateTime timestamp, final float value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public LocalDateTime getTimestamp() {
            return this.timestamp;
        }

        public float getValue() {
            return this.value;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "RawRateEvent(timestamp=" + this.getTimestamp() + ", value=" + this.getValue() + ")";
        }

    }
