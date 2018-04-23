package pl.edu.mimuw.weather.event;

import javafx.beans.binding.Binding;

import rx.Observable;
        import rx.Observable.Transformer;
        import rx.Subscription;
        import rx.functions.Func1;
        import rx.javafx.sources.CompositeObservable;
        import rx.schedulers.JavaFxScheduler;
        import rx.schedulers.Schedulers;
        import rx.subscribers.JavaFxSubscriber;

public class EventStream {

    public static class WrappedObservable<T> {
        final Observable<T> inner;

        public <U> WrappedObservable<T> andOn(Observable<U> observable, Func1<? super U, ? extends T> mapper) {
            return new WrappedObservable<>(inner.mergeWith(observable.map(mapper)));
        }

        public <U> WrappedObservable<T> andOn(Observable<U> observable, T value) {
            return new WrappedObservable<>(inner.mergeWith(fixedMap(observable, value)));
        }

        public Observable<T> toObservable() {
            return inner;
        }

        public Binding<T> toBinding() {
            return binding(inner);
        }

        public WrappedObservable(final Observable<T> inner) {
            this.inner = inner;
        }
    }

    private static EventStream instance = new EventStream();
    private CompositeObservable<Event> composite = new CompositeObservable<>();

    public static EventStream eventStream() {
        return instance;
    }

    public Observable<Event> events() {
        return eventStream().composite.toObservable();
    }

    @SuppressWarnings("unchecked")
    public static Subscription joinStream(Observable<? extends Event> observable) {
        final CompositeObservable<Event> local = eventStream().composite;
        synchronized (local) {
            return local.addAll((Observable<Event>) observable);
        }
    }

    public static Subscription joinBackpressuredStream(Observable<? extends Event> observable) {
        return joinStream(observable.onBackpressureLatest());
    }

    public static <T> Binding<T> binding(Observable<T> observable) {
        return JavaFxSubscriber.toBinding(observable);
    }

    public static <T> Observable<T> fixedMap(Observable<?> observable, T value) {
        return observable.map(ignore -> value);
    }

    public static <T, U> WrappedObservable<U> onEvent(Observable<T> observable, Func1<? super T, ? extends U> mapper) {
        return new WrappedObservable<>(observable.map(mapper));
    }

    public static <T, U> WrappedObservable<U> onEvent(Observable<T> observable, U value) {
        return new WrappedObservable<>(fixedMap(observable, value));
    }

    public Observable<Event> eventsInFx() {
        return events().compose(fxTransformer());
    }

    public Observable<Event> eventsInIO() {
        return events().compose(ioTransformer());
    }

    private <T> Transformer<T, T> fxTransformer() {
        return obs -> obs.observeOn(JavaFxScheduler.getInstance());
    }

    private <T> Transformer<T, T> ioTransformer() {
        return obs -> obs.observeOn(Schedulers.io());
    }
}

