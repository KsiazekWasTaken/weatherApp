package pl.edu.mimuw.weather;

import static javafx.scene.paint.Color.TRANSPARENT;
import static pl.edu.mimuw.weather.event.EventStream.eventStream;
import static pl.edu.mimuw.weather.event.EventStream.joinStream;

import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import rx.Observable;
import rx.Subscription;
import rx.observables.JavaFxObservable;
import com.jfoenix.controls.JFXDecorator;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.edu.mimuw.weather.event.Event;
import pl.edu.mimuw.weather.event.SettingsRequestEvent;
import pl.edu.mimuw.weather.event.WeatherEvent;
import pl.edu.mimuw.weather.network.DataSource;
import pl.edu.mimuw.weather.network.PollutionDataSource;
import pl.edu.mimuw.weather.network.WeatherDataSource;
import rx.Observable;
import rx.Subscription;
import rx.observables.JavaFxObservable;

import java.io.IOException;
import java.util.*;

/**
 * Created by HyperWorks on 2017-06-13.
 */
public class AppMain extends Application {
    private class DialogControllerBase {
        @FXML
        JFXDialog dialog;

        @FXML
        Button acceptButton;

        @FXML
        Button cancelButton;

        void initialize() {
            JavaFxObservable.actionEventsOf(cancelButton).subscribe(ignore -> {
                dialog.close();
            });
        }

        void show(StackPane pane) {
            dialog.show(pane);
        }

    }

    private class CloseDialogController extends DialogControllerBase {
        @FXML
        void initialize() {
            super.initialize();

            JavaFxObservable.actionEventsOf(acceptButton).subscribe(ignore -> {
                log.info("Exiting...");
                AppMain.this.mainStage.close(); // This should terminate the
                // application
                System.exit(0); // Just for sure
            });
        }
    }

    private class SettingsDialogController extends DialogControllerBase {
        @FXML
        ChoiceBox cBox;

        @FXML
        Pane content;

        @FXML
        void initialize() {

            super.initialize();
            cBox.setItems(FXCollections.observableArrayList("openweathermap.org", "meteo.waw.pl"));
            cBox.getSelectionModel().select(0);
            cBox.setStyle("-fx-focus-color: transparent;");
            cBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                }
            });
            JavaFxObservable.actionEventsOf(acceptButton).subscribe(ignore -> {
                try {
					AppMain.this.sourceStreams.stream().forEach(Subscription::unsubscribe);
					AppMain.this.sourceStreams.clear();
                    AppMain.this.setupDataSources(cBox.getValue().toString());
                } finally {
                    dialog.close();
                }
            });
        }

    }

    private DialogControllerBase closeDialogController;
    private DialogControllerBase settingsDialogController;
    private Stage mainStage;
    private static final String FXML_MAIN_FORM_TEMPLATE = "/fxml/weather.fxml";
    private static final String FXML_CLOSE_DIALOG_TEMPLATE = "/fxml/close-dialog.fxml";
    private static final String FXML_SETTINGS_DIALOG_TEMPLATE = "/fxml/settings-dialog.fxml";
    private static final String METEO = "meteo.waw.pl";

    private static final String FONT_CSS = "/css/jfoenix-fonts.css";
    private static final String MATERIAL_CSS = "/css/jfoenix-design.css";
    private static final String JFX_CSS = "/css/jfx.css";

    private static final Integer defaultPollInterval = 60/* *5 */;
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AppMain.class);
    private List<Subscription> sourceStreams = new LinkedList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage=primaryStage;
        setupDataSources();
        setupEventHandler();
        Parent pane = FXMLLoader.load(AppMain.class.getResource("/fxml/weather.fxml"));
        JFXDecorator decorator = new JFXDecorator(mainStage, pane, false, false, true);
        ObservableList<Node> buttonsList = ((Pane) decorator.getChildren().get(0)).getChildren();
        buttonsList.get(buttonsList.size() - 1).getStyleClass().add("close-button");
        decorator.setOnCloseButtonAction(this::onClose);

        Scene scene = new Scene(decorator);
        scene.getStylesheets().addAll(AppMain.class.getResource(FONT_CSS).toExternalForm(),
                AppMain.class.getResource(MATERIAL_CSS).toExternalForm(),
                AppMain.class.getResource(JFX_CSS).toExternalForm());
        scene.setFill(TRANSPARENT);
        mainStage.setScene(scene);
        mainStage.setWidth(250);
        mainStage.setHeight(385);
        mainStage.setResizable(false);
        mainStage.show();
    }

    private void onClose() {
        log.info("onClose");

        if (closeDialogController == null) {
            closeDialogController = new CloseDialogController();
            createDialog(closeDialogController, FXML_CLOSE_DIALOG_TEMPLATE);
        }

        closeDialogController.show(getMainPane());
    }

    private static void setupTextRendering() {
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "true");
    }

    private static void setupExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(
                (t, e) -> log.error("Uncaught exception in thread \'" + t.getName() + "\'", e));
    }

    public static void main(String args[]){
        setupTextRendering();
        setupExceptionHandler();
        Platform.setImplicitExit(true);
        Application.launch(AppMain.class, args);
    }

    private void setupDataSources(){
        setupDataSources(defaultPollInterval);
    }

    private void setupDataSources(Integer pollInterval){
        setupDataSources(pollInterval, "");
    }
    private void setupDataSources(String source){
        setupDataSources(defaultPollInterval, source);
    }


    private void setupDataSources(Integer pollInterval, String source){
        DataSource[] sources = { new WeatherDataSource(),
                new PollutionDataSource()};
        for (DataSource src: sources) {
            sourceStreams.add(joinStream(src.dataSourceStream(pollInterval, source)));
        }
    }

    private void setupEventHandler() {
        Observable<Event> events = eventStream().events();
        events.ofType(WeatherEvent.class).subscribe(log::info);

        events.ofType(SettingsRequestEvent.class).subscribe(e -> onSettingsRequested());
    }

    private StackPane getMainPane() {
        return (StackPane) mainStage.getScene().getRoot().lookup("#main");
    }

    private void createDialog(Object dialogController, String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(AppMain.class.getResource(fxmlPath));
        loader.setController(dialogController);
        try {
            loader.load();
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    private void onSettingsRequested() {
        log.info("onSettingsRequested");

        if (settingsDialogController == null) {
            settingsDialogController = new SettingsDialogController();
            createDialog(settingsDialogController, "/fxml/weather-dialog.fxml");
        }

        settingsDialogController.show(getMainPane());
    }

}
