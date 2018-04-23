package pl.edu.mimuw.weather.network;

import com.google.gson.JsonObject;
import io.reactivex.netty.RxNetty;
import pl.edu.mimuw.weather.event.WeatherEvent;
import rx.Observable;
import rx.exceptions.Exceptions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HyperWorks on 2017-06-13.
 */
public class WeatherDataSource extends DataSource {

    //public static final String URL_SOURCE = "http://api.openweathermap.org/data/2.5/forecast?id=756135&APPID=2069e814725bd889ebbf9468ee29c365";
    public static final String URL_SOURCE = "http://api.openweathermap.org/data/2.5/forecast?id=756135&APPID=2069e814725bd889ebbf9468ee29c365";

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WeatherDataSource.class);
    private static final float absolute0 = -273.15f;
    private static final String WEATHER_JSON_KEY = "weather";
    private static final String SHORT_DESCR_JSON_KEY = "main";
    private static final String DESCR_JSON_KEY = "description";
    private static final String TEMP_JSON_KEY = "temp";
    private static final String MAIN_JSON_KEY = "main";
    private static final String LIST_JSON_KEY = "list";

    private float Kelvin2Celsius(float tempInKelvin){
        return (Math.round((tempInKelvin + absolute0)*100)/100f);
    }

   public Observable<WeatherEvent> makeRequest(){
        String url = URL_SOURCE;
        //url="http://api.nbp.pl/api/exchangerates/rates/c/";
        String myurl = "http://api.openweathermap.org/data/2.5/weather?id=756135&APPID=2069e814725bd889ebbf9468ee29c365";
        return RxNetty.createHttpRequest( // create an HTTP request using RxNetty
                prepareHttpGETRequest(myurl))
                .compose(this::unpackResponse) // extract response body to a
                // string
                .map(JsonHelper::asJsonObject) // convert this string to a
                // JsonObject
                .map(jsonObject -> {
                    JsonObject weatherJsonObject = jsonObject.get("weather").getAsJsonArray().get(0)
                            .getAsJsonObject();
                    JsonObject cloudsJsonObject = jsonObject.get("clouds").getAsJsonObject();
                    float clouds = cloudsJsonObject.get("all").getAsFloat();
                    String shortdesc = weatherJsonObject.get("main").getAsString();
                    String desc = weatherJsonObject.get("description").getAsString();
                    JsonObject mainJsonObject = jsonObject.get("main").getAsJsonObject();
                    float temperature = mainJsonObject.get("temp").getAsFloat();
                    float pressure = mainJsonObject.get("pressure").getAsFloat();
                    float humidity = mainJsonObject.get("humidity").getAsFloat();
                    JsonObject windJsonObject = jsonObject.get("wind").getAsJsonObject();
                    float windDeg = windJsonObject.get("deg").getAsFloat();
                    float windSpeed = windJsonObject.get("speed").getAsFloat();
                    return new WeatherEvent(shortdesc, desc, Kelvin2Celsius(temperature), windDeg, windSpeed,
                            pressure, humidity, clouds);
                });
    }

   private class BitcoinRateNotFoundException extends Exception {
       private static final long serialVersionUID = 1L;
   };

   private static final Pattern MAGIC_TEMP = Pattern.compile("temperatura\\s*[^<]*<[^>]*>\\s*(\\d*),[0-9]",
           Pattern.CASE_INSENSITIVE);
   private static final Pattern MAGIC_HUM = Pattern.compile("wilgotność\\s*[^<]*<[^>]*>\\s*(\\d*),[0-9]",
           Pattern.CASE_INSENSITIVE);
   private static final Pattern MAGIC_PRES = Pattern.compile("ciśnienie\\s*[^<]*<[^>]*>\\s*(\\d*),[0-9]",
           Pattern.CASE_INSENSITIVE);
   private static final Pattern MAGIC_SPEED = Pattern.compile("wiatr\\s*[^<]*<[^>]*>\\s*(\\d*),[0-9]",
           Pattern.CASE_INSENSITIVE);
   private static final Pattern MAGIC_DEGREE = Pattern.compile("<span\\s*id=\\\"PARAM_0_WDABBR\">..",
           Pattern.CASE_INSENSITIVE);

   private float getDirection(String dir){
       switch (dir){
           case "N":
               return 0f;
           case "NW":
               return (float)360-45;
           case "NE":
               return 45f;
           case "E":
               return 90f;
           case "SE":
               return 135f;
           case "S":
               return 180f;
           case "SW":
               return 225f;
           case "W":
               return 270f;
           default:
               return -1f;
       }
   }

   public Observable<WeatherEvent> makeRequest(String source){
       String url="http://www."+source;
       return RxNetty.createHttpRequest(prepareHttpGETRequest(url)).compose(this::unpackResponse).map(htmlSource -> {
           Matcher m = MAGIC_TEMP.matcher(htmlSource);
           Matcher mH = MAGIC_HUM.matcher(htmlSource);
           Matcher mP = MAGIC_PRES.matcher(htmlSource);
           Matcher mS = MAGIC_SPEED.matcher(htmlSource);
           Matcher mD = MAGIC_DEGREE.matcher(htmlSource);
           if (m.find() && mH.find() && mP.find() && mS.find() && mD.find()) {
               String s = m.group(0).split(">")[1].replace(',','.');
               String sH = mH.group(0).split(">")[1].replace(',','.');
               String sP = mP.group(0).split(">")[1].replace(',','.');
               String sS = mS.group(0).split(">")[1].replace(',','.');
               String sD = mD.group(0);
               String dir = sD.split(">")[1].replace(',','.');
               return new WeatherEvent("shortdesc", "", Float.parseFloat(s),
                       getDirection(dir), Float.parseFloat(sS),
                       Float.parseFloat(sP), Float.parseFloat(sH), -1f);
           }
           else throw Exceptions.propagate(new WeatherDataSource.BitcoinRateNotFoundException());
       });
   }
}

