package pl.edu.mimuw.weather.event;

import java.time.LocalDateTime;
import java.util.Currency;

/**
 * Created by HyperWorks on 2017-06-07.
 */

public final class WeatherEvent extends Event {
    private final String weather;
    private final String descr;
    private final float temp;
    private final float pressure;
    private final float humidity;
    private final float clouds;
    private final WindData wind;
    private final LocalDateTime timestamp;

    public WeatherEvent(final String weather, final String descr, final float temp, final float deg, final float speed,
                        final float pressure, final float humidity, final float clouds) {
        timestamp = LocalDateTime.now();
        this.weather = weather;
        this.descr = descr;
        this.temp = temp;
        wind = new WindData(deg, speed);
        this.humidity=humidity;
        this.pressure=pressure;
        this.clouds=clouds;


    }

    public float getPressure() {
        return pressure;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getClouds() {
        return clouds;
    }

    public String getUnit() {
        return "C";
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getWeather() {
        return weather;
    }

    public String getDescr() {
        return descr;
    }

    public float getTemp() {
        return temp;
    }

    public float getWindDegree() {
        return wind.getDegree();
    }

    public float getWindSpeed() {
        return wind.getSpeed();
    }

    @Override
    public String toString() {
        return "WeatherEvent{" +
                "weather='" + weather + '\'' +
                ", descr='" + descr + '\'' +
                ", temp=" + temp +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", clouds=" + clouds +
                ", wind=" + wind +
                '}';
    }
}


