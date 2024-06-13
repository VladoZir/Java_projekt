package hr.java.waterUsageJavaFxApplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public class SignUpController {
    private static final String USER_INFO_FILE = "dat/userInfo.txt";
    @FXML
    private TextField userNameTextField;
    @FXML
    private PasswordField passwordPasswordField;
    @FXML
    private ChoiceBox<String> userTypeChoiceBox;

    public void initialize() {
        List<String> userTypeList = List.of("Gost", "Administrator");
        ObservableList<String> userTypeObservableList = FXCollections.observableArrayList(userTypeList);
        userTypeChoiceBox.setItems(userTypeObservableList);
    }

    public void signUp() {
        String userName = userNameTextField.getText();
        String password = passwordPasswordField.getText();
        Optional<String> userTypeOptional = Optional.ofNullable(userTypeChoiceBox.getValue());

        if (userName.isEmpty()) {
            HelloApplication.logger.error("Korisničko ime ne smije biti prazno!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješna prijava!");
            alert.setContentText("Korisničko ime ne smije biti prazno!");
            alert.showAndWait();
        } else if (password.isEmpty()) {
            HelloApplication.logger.error("Lozinka ne smije biti prazna!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješna prijava!");
            alert.setContentText("Lozinka ne smije biti prazna!");
            alert.showAndWait();
        } else if (password.contains(" ")) {
            HelloApplication.logger.error("Lozinka ne smije sadržavati razmak!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješna prijava!");
            alert.setContentText("Lozinka ne smije sadržavati razmak!");
            alert.showAndWait();
        } else if (userName.length() < 3) {
            HelloApplication.logger.error("Korisničko ime mora sadržavati barem 3 znaka!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješna prijava!");
            alert.setContentText("Korisničko ime mora sadržavati barem 3 znaka!");
            alert.showAndWait();
        } else if (password.length() < 3) {
            HelloApplication.logger.error("Lozinka mora sadržavati barem 3 znaka!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješna prijava!");
            alert.setContentText("Lozinka mora sadržavati barem 3 znaka!");
            alert.showAndWait();
        } else if (!(userTypeOptional.isPresent())) {
            HelloApplication.logger.error("Morate odabrati tip korisnika!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješna prijava!");
            alert.setContentText("Morate odabrati tip korisnika!");
            alert.showAndWait();
        } else {

            String hashedPassword = hashPassword(password);

            String userType = userTypeOptional.get();
            File userInfoFile = new File(USER_INFO_FILE);
            try (FileOutputStream fos = new FileOutputStream(userInfoFile, true);
                 PrintWriter pw = new PrintWriter(fos)) {
                pw.println(userName + ";" + hashedPassword + ";" + userType);
            } catch (Exception e) {
                HelloApplication.logger.error("Pogreška prilikom spremanja korisničkih podataka!", e);
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Uspješna registracija!");
            alert.setHeaderText("Uspješno ste se registrirali u sustav!");
            alert.setContentText("Nastavi na ekran za prijavu!");
            alert.showAndWait();

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

    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for(byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            HelloApplication.logger.error("Pogreška prilikom hashiranja lozinke!", e);
            return null;
        }
    }

}
