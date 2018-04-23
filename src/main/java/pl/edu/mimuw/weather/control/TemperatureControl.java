package pl.edu.mimuw.weather.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import pl.edu.mimuw.weather.event.RawWeatherEvent;
import rx.Observable;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public class TemperatureControl extends WeatherValueControl {
    private final char DEG = '\u00B0';
    protected StringProperty suffixProperty = new SimpleStringProperty(DEG+"C");

    public void setSource(Observable<RawWeatherEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }

            //textControl.setText(format.format(e.getValue()));
            textControl.setText(String.format("%.1f",e.getValue()));
        });
        sourceProperty.set(source);
    }



    protected void createContentControls() {
        getChildren().remove(noDataIcon);

        textControl = new Text();
        textControl.getStyleClass().add("temp-value");

        suffixLabel = new Text();
        suffixLabel.textProperty().bind(suffixProperty);
        suffixLabel.getStyleClass().add("C-label");

        innerContainer = new HBox();
        innerContainer.setAlignment(Pos.CENTER);
        innerContainer.getStyleClass().add("value-container");
        innerContainer.getChildren().addAll(textControl, suffixLabel);

        getChildren().add(innerContainer);
    }
}
