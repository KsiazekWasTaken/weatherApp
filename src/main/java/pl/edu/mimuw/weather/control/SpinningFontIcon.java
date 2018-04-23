package pl.edu.mimuw.weather.control;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.effect.MotionBlur;
import javafx.util.Duration;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by HyperWorks on 2017-06-17.
 */
public class SpinningFontIcon extends FontIcon {

        private static final Duration duration = Duration.seconds(2);

        private RotateTransition transition;

        public SpinningFontIcon() {
            super();
            blurWorkaround();
            setUpAnimation();
        }

        public SpinningFontIcon(String iconCode) {
            super(iconCode);
            blurWorkaround();
            setUpAnimation();
        }

        public SpinningFontIcon(Ikon iconCode) {
            super(iconCode);
            blurWorkaround();
            setUpAnimation();
        }

        public void stop() {
            if (transition != null) {
                transition.stop();
            }
        }

        public void play() {
            if (transition == null) {
                setUpAnimation();
            }
        }

        protected void setUpAnimation() {
            stop();
            transition = new RotateTransition(duration, this);
            transition.setByAngle(360);
            transition.setFromAngle(0);
            transition.setInterpolator(Interpolator.EASE_IN);
            transition.setCycleCount(1);
            transition.setOnFinished(e -> {
                transition = new RotateTransition(duration, this);
                transition.setByAngle(360);
                transition.setFromAngle(0);
                transition.setInterpolator(Interpolator.LINEAR);
                transition.setCycleCount(-1);

                transition.play();
            });

            transition.play();
        }

        private void blurWorkaround() {
            String pipelineName = getCurrentGraphicsPipelineName();
            if (pipelineName.endsWith("D3DPipeline") || pipelineName.endsWith("ES2Pipeline")) {
                this.setEffect(new MotionBlur(0, 0));
            }

        }

        private static String getCurrentGraphicsPipelineName() {
            try {
                Class<?> clazz = Class.forName("com.sun.prism.GraphicsPipeline");
                Method m = clazz.getDeclaredMethod("getPipeline");
                return m.invoke(null).getClass().getName();
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                return "";
            }
        }

}
