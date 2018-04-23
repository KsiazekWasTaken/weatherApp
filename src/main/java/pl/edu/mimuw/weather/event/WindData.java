package pl.edu.mimuw.weather.event;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public final class WindData {
    private final float degree;

    private final float speed;

    public float getDegree() {
        return degree;
    }

    public float getSpeed() {
        return speed;
    }

    public WindData(float deg, float s){
        degree=deg;
        speed=s;
    }

    @Override
    public String toString() {
        return "Wind{" +
                "degree=" + degree +
                ", speed=" + speed +
                '}';
    }
}
