package pl.edu.mimuw.weather.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.util.Duration;
import pl.edu.mimuw.weather.control.*;
import pl.edu.mimuw.weather.event.*;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.JavaFxObservable;
import rx.schedulers.JavaFxScheduler;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static pl.edu.mimuw.weather.event.EventStream.*;

/**
 * Created by HyperWorks on 2017-06-16.
 */
public class AppController {

    @FXML
    private WeatherValueControl temperatureControlAtb;

    @FXML
    private Button refreshButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Node workingIcon;

    @FXML
    private Node errorIcon;

    @FXML private Label time;
    @FXML private Label date;

    private int minute;
    private int hour;
    private int second;


    protected String formatPattern = "00";
    protected DecimalFormat format = new DecimalFormat(formatPattern);

    @FXML
    private void initialize() {

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            second = cal.get(Calendar.SECOND);
            minute = cal.get(Calendar.MINUTE);
            hour = cal.get(Calendar.HOUR_OF_DAY);
            time.setText(hour + ":" + format.format(minute) + ":" + format.format(second));
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        initializeSettingsHandler();
        initalizeRefreshHandler();
        initializeStatus();

    }

    @FXML
    private BriefValueControl descriptionControlAtb;

    @FXML
    private TimestampControl timestampControlAtb;

    @FXML
    private BriefValueControl fullDescriptionControlAtb;

    @FXML
    private Pollution10Control pM10ControlAtb;

    @FXML
    private Pollution2_5Control pM25ControlAtb;

    @FXML
    private HumidityValueControl humidityControlAtb;

    @FXML
    private PressureValueControl pressureControlAtb;

    @FXML
    private CloudValueControl cloudControlAtb; /*xD*/

    @FXML
    private WindSpeedControl windSpeedControlAtb; /*xD*/

    @FXML
    private WindDirectionIconControl windDegreeControlAtb;

    @FXML
    private IconControl iconControlAtb;

    @FXML
    public Observable<RawWeatherEvent> getHumidityStream(){
        return getStream(WeatherEvent::getHumidity);
    }

    @FXML
    public Observable<RawWeatherEvent> getPressureStream(){
        return getStream(WeatherEvent::getPressure);
    }

    @FXML
    public Observable<RawWeatherEvent> getCloudStream(){
        return getStream(WeatherEvent::getClouds);
    }

    @FXML
    public Observable<RawWeatherEvent> getWindSpeedStream(){return getStream(WeatherEvent::getWindSpeed);}

    @FXML
    public Observable<RawWeatherEvent> getWindDegreeStream(){return getStream(WeatherEvent::getWindDegree);}

    @FXML
    public Observable<StringWeatherEvent> getDescriptionStream(){
        return getStringStream(WeatherEvent::getWeather);
    }

    @FXML
    public Observable<StringWeatherEvent> getFullDescriptionStream(){
        return getStringStream(WeatherEvent::getDescr);
    }

    @FXML
    public Observable<RawWeatherEvent> getTemperatureStream() {
        return getStream(WeatherEvent::getTemp);
    }

    @FXML
    public Observable<RawWeatherEvent> getPm10Stream() {
        return getPollutionStream(PollutionEvent::getPM10);
    }

    @FXML
    public Observable<RawWeatherEvent> getPm25Stream() {
        return getPollutionStream(PollutionEvent::getPM2_5);
    }

    private Observable<RawWeatherEvent> getStream(Func1<WeatherEvent, Float> extractor) {
        return eventStream().eventsInFx().ofType(WeatherEvent.class)
                //.filter(e -> e.getUnit().equals("C"))
                .map(e -> new RawWeatherEvent(e.getTimestamp(), extractor.call(e)));
    }

    private Observable<RawWeatherEvent> getPollutionStream(Func1<PollutionEvent, Float> extractor) {
        return eventStream().eventsInFx().ofType(PollutionEvent.class)
                .map(e -> new RawWeatherEvent(e.getTimestamp(), extractor.call(e)));
    }

    private Observable<StringWeatherEvent> getStringStream(Func1<WeatherEvent, String> extractor) {
        return eventStream().eventsInFx().ofType(WeatherEvent.class)
                .map(e -> new StringWeatherEvent(e.getTimestamp(), extractor.call(e)));
    }
    private void initalizeRefreshHandler() {
        joinStream(JavaFxObservable.actionEventsOf(refreshButton).map(e -> new RefreshRequestEvent()));
    }

    private void initializeSettingsHandler() {
        joinStream(JavaFxObservable.actionEventsOf(settingsButton).map(e -> new SettingsRequestEvent()));
    }

    private void initializeStatus() {
        Observable<Event> events = eventStream().eventsInFx();

        // Basically, we keep track of the difference between issued requests
        // and completed requests
        // If this difference is > 0 we display the spinning icon...
        workingIcon.visibleProperty()
                .bind(binding(events.ofType(NetworkRequestIssuedEvent.class).map(e -> 1) // Every
                        // issued
                        // request
                        // contributes
                        // +1
                        .mergeWith(events.ofType(NetworkRequestFinishedEvent.class).map(e -> -1) // Every
                                // completed
                                // request
                                // contributes
                                // -1
                                .delay(2, TimeUnit.SECONDS, JavaFxScheduler.getInstance())) // We delay
                        // completion
                        // events for 2
                        // seconds so
                        // that the
                        // spinning icon
                        // is always
                        // displayed for
                        // at least 2
                        // seconds and
                        // it does not
                        // blink
                        .scan(0, (x, y) -> x + y).map(v -> v > 0))

                );

		/*
		 * This should show the error icon when an error event arrives and hides
		 * the icon after 30 seconds unless another error arrives
		 */
        Observable<ErrorEvent> errors = events.ofType(ErrorEvent.class);
        errorIcon.visibleProperty()
                .bind(onEvent(errors, true).andOn(
                        errors.throttleWithTimeout(30, TimeUnit.SECONDS, JavaFxScheduler.getInstance()),
                        false).toBinding());
    }
}
