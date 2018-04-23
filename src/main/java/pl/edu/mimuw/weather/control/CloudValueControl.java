package pl.edu.mimuw.weather.control;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import pl.edu.mimuw.weather.event.RawWeatherEvent;
import rx.Observable;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public class CloudValueControl extends WeatherValueControl {
    protected StringProperty prefixProperty = new SimpleStringProperty("Cloud cover: ");
    protected StringProperty suffixProperty = new SimpleStringProperty("%");

    public void setSource(Observable<RawWeatherEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }
            String s = format.format(e.getValue());
            if(s.equals("-1")) s="-";
            textControl.setText(s);
        });
        sourceProperty.set(source);
    }

    protected void createContentControls() {
        getChildren().remove(noDataIcon);

        textControl = new Text();
        textControl.getStyleClass().add("rate-value");


        prefixLabel = new Text();
        prefixLabel.textProperty().bind(prefixProperty);
        prefixLabel.getStyleClass().add("helper-label");

        suffixLabel = new Text();
        suffixLabel.textProperty().bind(suffixProperty);
        suffixLabel.getStyleClass().add("helper-label");

        innerContainer = new HBox();
        innerContainer.setAlignment(Pos.BOTTOM_CENTER);
        innerContainer.getStyleClass().add("value-container");
        innerContainer.getChildren().addAll(prefixLabel, textControl, suffixLabel);

        getChildren().add(innerContainer);
    }
}
