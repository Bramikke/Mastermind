package gui;

import domein.DomeinController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.stage.Stage;

public class StartUp extends Application {

    @Override
    public void start(Stage stage) {
        DomeinController controller = new DomeinController();
        Scene scene = new Scene(new HoofdPaneel(controller), 600, Control.USE_COMPUTED_SIZE);
        scene.getStylesheets().add("/gui/styles.css");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String... args) {
        Application.launch(StartUp.class, args);
    }
}
