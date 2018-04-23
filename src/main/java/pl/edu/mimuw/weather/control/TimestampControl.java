package pl.edu.mimuw.weather.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;
import pl.edu.mimuw.weather.event.StringWeatherEvent;
import rx.Observable;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public class TimestampControl extends Pane {
    private FontIcon currentIcon = new FontIcon();

    private FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);
    private HBox innerContainer;
    private Text textControl;



    private ObjectProperty<Observable<StringWeatherEvent>> sourceProperty = new SimpleObjectProperty<>();
    //private StringProperty suffixProperty = new SimpleStringProperty("C");
    //private StringProperty titleProperty = new SimpleStringProperty("-");

    public Observable<StringWeatherEvent> getSource() {
        return sourceProperty.get();
    }

    public void setSource(Observable<StringWeatherEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }
            FontIcon weatherIcon = new FontIcon();
            textControl.setText("Last update: " +e.getTimestamp().toString());

        });
        sourceProperty.set(source);
    }
    public FontIcon getIcon(){
        return currentIcon;
    }


    public TimestampControl() {
        noDataIcon.getStyleClass().add("small-timestamp");
        getChildren().add(noDataIcon);
    }

    private void createContentControls() {
        getChildren().remove(noDataIcon);

        textControl = new Text();
        textControl.getStyleClass().add("small-timestamp");
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
