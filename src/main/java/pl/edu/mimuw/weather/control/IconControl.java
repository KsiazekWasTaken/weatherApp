package pl.edu.mimuw.weather.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;
import pl.edu.mimuw.weather.event.StringWeatherEvent;
import rx.Observable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public class IconControl extends StackPane {
    private FontIcon currentIcon = new FontIcon();

    private FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);
    private HBox innerContainer;



    private ObjectProperty<Observable<StringWeatherEvent>> sourceProperty = new SimpleObjectProperty<>();

    public Observable<StringWeatherEvent> getSource() {
        return sourceProperty.get();
    }

    private boolean isDay(){
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        if(hour<22&&hour>5){
            /*wersja letnia ;d*/
            return true;
        }
        else return false;
    }

    public void setSource(Observable<StringWeatherEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }
            getChildren().removeAll();
            String weather = e.getValue();
            switch (weather){
                case "clear sky":
                    if(isDay()) currentIcon = new FontIcon(WeatherIcons.DAY_SUNNY);
                    else currentIcon = new FontIcon(WeatherIcons.NIGHT_CLEAR);
                    break;
                case "few clouds":
                    if(isDay()) currentIcon= new FontIcon(WeatherIcons.CLOUDY);
                    else currentIcon= new FontIcon(WeatherIcons.NIGHT_ALT_CLOUDY);
                    break;
                case "scattered clouds":
                    if(isDay()) currentIcon= new FontIcon(WeatherIcons.DAY_CLOUDY_HIGH);
                    else currentIcon= new FontIcon(WeatherIcons.NIGHT_ALT_CLOUDY_HIGH);
                    break;
                case "broken clouds":
                    if(isDay()) currentIcon= new FontIcon(WeatherIcons.DAY_CLOUDY);
                    else currentIcon= new FontIcon(WeatherIcons.NIGHT_ALT_PARTLY_CLOUDY);
                    break;
                case "shower rain":
                    currentIcon= new FontIcon(WeatherIcons.SHOWERS);
                    break;
                case "rain":
                    currentIcon= new FontIcon(WeatherIcons.RAIN);
                    break;
                case "thunderstorm":
                    currentIcon= new FontIcon(WeatherIcons.THUNDERSTORM);
                    break;
                case "snow":
                    currentIcon= new FontIcon(WeatherIcons.SNOW);
                    break;
                case "mist":
                    currentIcon= new FontIcon(WeatherIcons.FOG);
                    break;
                default:
                    if(isDay()) currentIcon= new FontIcon(WeatherIcons.CLOUD);
                    else currentIcon = new FontIcon(WeatherIcons.NIGHT_CLEAR);
                    break;
            }
            currentIcon.getStyleClass().add("myStyle");
            while(getChildren().size()!=0) getChildren().remove(0);
            getChildren().add(currentIcon);

        });
        sourceProperty.set(source);
    }
    public FontIcon getIcon(){
        return currentIcon;
    }


    public IconControl() {
        noDataIcon.getStyleClass().add("no-data");
        getChildren().add(noDataIcon);
    }

    private void createContentControls() {
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
