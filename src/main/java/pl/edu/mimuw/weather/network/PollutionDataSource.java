package pl.edu.mimuw.weather.network;

import com.google.gson.JsonObject;
import io.reactivex.netty.RxNetty;
import pl.edu.mimuw.weather.event.*;
import rx.Observable;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public class PollutionDataSource extends DataSource {

    public static final String URL_SOURCE = "http://api.openweathermap.org/data/2.5/forecast?id=756135&APPID=2069e814725bd889ebbf9468ee29c365";

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WeatherDataSource.class);
    private static final float absolute0 = -273.15f;
    private static final String WEATHER_JSON_KEY = "weather";
    private static final String SHORT_DESCR_JSON_KEY = "main";
    private static final String DESCR_JSON_KEY = "description";
    private static final String TEMP_JSON_KEY = "temp";
    private static final String MAIN_JSON_KEY = "main";
    private static final String LIST_JSON_KEY = "list";


    private static final int DEFAULT_POLL_INTERVAL = 60;
    private static final int INITIAL_DELAY = 3;
    private static final int TIMEOUT = 20;

    public Observable<PollutionEvent> makeRequest(){
        String url = URL_SOURCE;
        String myurl = "http://powietrze.gios.gov.pl/pjp/current/getAQIDetailsList?param=AQI";
        return RxNetty.createHttpRequest(JsonHelper.withJsonHeader(prepareHttpGETRequest(myurl)))
                .compose(this::unpackResponse).map(JsonHelper::asJsonArray).map(jsonArray -> {
                    boolean setWarsaw = false;
                    int index=0;
                    while(!setWarsaw && (index<jsonArray.size())){
                        int stationId = jsonArray.get(index).getAsJsonObject().get("stationId").getAsInt();
                        if(stationId==544){
                            setWarsaw=true;
                        }
                        index++;
                    }
                    JsonObject values = jsonArray.get(index-1).getAsJsonObject().get("values").getAsJsonObject();
                    float PM10 = values.get("PM10").getAsFloat();
                    float PM2_5 = values.get("PM2.5").getAsFloat();
                    return new PollutionEvent(PM2_5, PM10);
                });
    }

    public Observable<PollutionEvent> makeRequest(String url){
        return makeRequest();
    }



}
