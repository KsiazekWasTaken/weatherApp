package pl.edu.mimuw.weather.control;

import javafx.animation.RotateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;
import pl.edu.mimuw.weather.event.RawWeatherEvent;
import pl.edu.mimuw.weather.event.StringWeatherEvent;
import rx.Observable;

import java.util.Calendar;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public class WindDirectionIconControl extends WeatherValueControl {

    private static final Duration duration = Duration.seconds(0.3f);

    private RotateTransition transition;

    private FontIcon currentIcon = new FontIcon();

    private FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);
    private HBox innerContainer;



    private ObjectProperty<Observable<RawWeatherEvent>> sourceProperty = new SimpleObjectProperty<>();

    public Observable<RawWeatherEvent> getSource() {
        return sourceProperty.get();
    }

    public void setSource(Observable<RawWeatherEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }
            getChildren().removeAll();
            float weather = e.getValue();
            currentIcon = new FontIcon(WeatherIcons.DIRECTION_UP);
            currentIcon.getStyleClass().add("myStyle2");
            while(getChildren().size()!=0) getChildren().remove(0);
            getChildren().add(currentIcon);
            transition = new RotateTransition(duration, currentIcon);
            transition.setByAngle(weather);
            transition.setFromAngle(0);
            transition.play();

        });
        sourceProperty.set(source);
    }
    public FontIcon getIcon(){
        return currentIcon;
    }


    public WindDirectionIconControl() {
        noDataIcon.getStyleClass().add("no-data");
        getChildren().add(noDataIcon);
    }

    protected void createContentControls() {
        getChildren().remove(noDataIcon);
        innerContainer = new HBox();
        innerContainer.getStyleClass().add("value-container");

        getChildren().add(innerContainer);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        if (noDataIcon.isVisible()) {
            noDataIcon.relocate((getWidth() - noDataIcon.getLayoutBounds().getWidth()) / 2,
                    (getHeight() - noDataIcon.getLayoutBounds().getHeight()) / 2);
        }

        if (innerContainer != null) {
            innerContainer.relocate((getWidth() - innerContainer.getLayoutBounds().getWidth()) / 2,
                    (getHeight() - innerContainer.getLayoutBounds().getHeight()) / 2);
        }

    }

}
