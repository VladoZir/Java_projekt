package hr.java.waterUsageJavaFxApplication;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.io.IOException;

public class MainScreenController {
    public void showLoginScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("loginScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 700, 500);
            scene.getStylesheets().add(getClass().getResource("mainScreen.css").toExternalForm());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }

    public void showSignUpScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("signUpScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 700, 500);
            scene.getStylesheets().add(getClass().getResource("mainScreen.css").toExternalForm());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }
}
