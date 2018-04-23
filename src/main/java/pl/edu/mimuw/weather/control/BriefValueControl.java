package pl.edu.mimuw.weather.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;
import pl.edu.mimuw.weather.event.RawWeatherEvent;
import pl.edu.mimuw.weather.event.StringWeatherEvent;
import rx.Observable;

import java.text.DecimalFormat;

import static javafx.scene.text.TextAlignment.LEFT;

/**
 * Created by HyperWorks on 2017-06-16.
 */
public class BriefValueControl extends Pane {
    private FontIcon currentIcon = new FontIcon();

    private FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);
    private HBox innerContainer;
    private Text textControl;



    private ObjectProperty<Observable<StringWeatherEvent>> sourceProperty = new SimpleObjectProperty<>();

    public Observable<StringWeatherEvent> getSource() {
        return sourceProperty.get();
    }

    public void setSource(Observable<StringWeatherEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }
            FontIcon weatherIcon = new FontIcon();
            textControl.setText(e.getValue());

        });
        sourceProperty.set(source);
    }
    public FontIcon getIcon(){
        return currentIcon;
    }


    public BriefValueControl() {
        noDataIcon.getStyleClass().add("no-data");
        getChildren().add(noDataIcon);
    }

    private void createContentControls() {
        getChildren().remove(noDataIcon);

        textControl = new Text();
        textControl.getStyleClass().add("rate-value");
        innerContainer = new HBox();
        innerContainer.getStyleClass().add("value-container");
        innerContainer.getChildren().addAll(textControl);

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
