package pl.edu.mimuw.weather.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;
import pl.edu.mimuw.weather.event.RawWeatherEvent;
import rx.Observable;

import java.text.DecimalFormat;

import static javafx.geometry.NodeOrientation.LEFT_TO_RIGHT;
import static javafx.scene.text.TextAlignment.LEFT;

/**
 * Created by HyperWorks on 2017-06-15.
 */
public class WeatherValueControl extends Pane {
        //private FontIcon currentIcon;

        protected FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);
        protected HBox innerContainer;
        protected Text prefixLabel;
        protected Text suffixLabel;
        protected String formatPattern = "0.##";
        protected DecimalFormat format = new DecimalFormat(formatPattern);
        protected Text textControl;

        protected StringProperty prefixProperty = new SimpleStringProperty();
        protected StringProperty suffixProperty = new SimpleStringProperty();
        protected StringProperty titleProperty = new SimpleStringProperty();


    protected ObjectProperty<Observable<RawWeatherEvent>> sourceProperty = new SimpleObjectProperty<>();

        public Observable<RawWeatherEvent> getSource() {
            return sourceProperty.get();
        }

        public void setSource(Observable<RawWeatherEvent> source) {
            source.subscribe(e -> {
                if (innerContainer == null) {
                    createContentControls();
                }

                textControl.setText(format.format(e.getValue()));
            });
            sourceProperty.set(source);
        }
        public WeatherValueControl() {
            noDataIcon.getStyleClass().add("no-data");
            getChildren().add(noDataIcon);
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
            innerContainer.getStyleClass().add("value-container");
            innerContainer.getChildren().addAll(prefixLabel, textControl, suffixLabel);

            getChildren().add(innerContainer);
        }

        @Override
        protected void layoutChildren() {
		/* Custom children positioning */
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
