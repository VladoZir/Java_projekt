package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.threads.GetTheLatestChangeThread;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class WelcomeScreenController {
    @FXML
    private Label userNameLabel;

    public void initialize() {
        userNameLabel.setText("Dobrodošli, " + LoginController.userNameGlobal + "!");

        GetTheLatestChangeThread getTheLatestChangeThread = new GetTheLatestChangeThread();
        Thread starter = new Thread(getTheLatestChangeThread);
        starter.start();

    }
}
