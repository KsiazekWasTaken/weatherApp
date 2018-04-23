package pl.edu.mimuw.weather.network;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import io.reactivex.netty.protocol.http.client.HttpClientRequest;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import pl.edu.mimuw.weather.event.*;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public abstract class DataSource {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DataSource.class);


    private static final int DEFAULT_POLL_INTERVAL = 60;
    private static final int INITIAL_DELAY = 3;
    private static final int TIMEOUT = 20;

    protected abstract Observable<? extends Event> makeRequest();
    protected abstract Observable<? extends Event> makeRequest(String source);


    protected HttpClientRequest<ByteBuf> prepareHttpGETRequest(String url) {
		/*
		 * As the name says, this creates an HTTP GET request (but does not send
		 * it, sending is done elsewhere).
		 */
        return HttpClientRequest.createGet(url);
    }

    protected Observable<String> unpackResponse(Observable<HttpClientResponse<ByteBuf>> responseObservable) {
		/*
		 * Extracts HTTP response's body to a plain Java string
		 */
        return responseObservable.flatMap(HttpClientResponse::getContent)
                .map(buffer -> buffer.toString(CharsetUtil.UTF_8));
    }

    private <T> Observable<Event> wrapRequest(Observable<T> observable) {
		/*
		 * Issues an HTTP query but emits an appropriate even before the query
		 * is made and another event when the query is completed. This allows us
		 * to give visual feedback (spinning icon) to the user during the
		 * request.
		 */
        return observable.flatMap(ignore -> Observable.concat(Observable.just(new NetworkRequestIssuedEvent()),
                makeRequest().timeout(TIMEOUT, TimeUnit.SECONDS).doOnError(log::error)
                        .cast(Event.class).onErrorReturn(ErrorEvent::new),
                Observable.just(new NetworkRequestFinishedEvent()))
        );
    }

    private <T> Observable<Event> wrapRequestAlternative(Observable<T> observable) {
		/*
		 * prototype for switching between sources
		 */
        return observable.flatMap(ignore -> Observable.concat(Observable.just(new NetworkRequestIssuedEvent()),
                makeRequest("meteo.waw.pl").timeout(TIMEOUT, TimeUnit.SECONDS).doOnError(log::error)
                        .cast(Event.class).onErrorReturn(ErrorEvent::new),
                Observable.just(new NetworkRequestFinishedEvent()))
        );
    }

    public Observable<? extends Event> dataSourceStream(Integer pollInterval, String source) {
        if (pollInterval == null) {
            pollInterval = DEFAULT_POLL_INTERVAL;
        }
        if(source.equals("meteo.waw.pl")) return fixedIntervalStream(pollInterval).compose(this::wrapRequestAlternative)
                .mergeWith(EventStream.eventStream().eventsInIO().ofType(RefreshRequestEvent.class).compose(this::wrapRequestAlternative));
        return fixedIntervalStream(pollInterval).compose(this::wrapRequest)
                .mergeWith(EventStream.eventStream().eventsInIO().ofType(RefreshRequestEvent.class).compose(this::wrapRequest));
    }

    protected Observable<Long> fixedIntervalStream(int pollInterval) {
        return Observable.interval(INITIAL_DELAY, pollInterval, TimeUnit.SECONDS, Schedulers.io());
    }



}
