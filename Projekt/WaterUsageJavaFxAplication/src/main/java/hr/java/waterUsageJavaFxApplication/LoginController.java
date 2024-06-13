package hr.java.waterUsageJavaFxApplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static hr.java.waterUsageJavaFxApplication.HelloApplication.logger;

public class LoginController {
    public static String userMode;
    public static String userNameGlobal;
    private static final String USER_INFO_FILE = "dat/userInfo.txt";
    @FXML
    private TextField userNameTextField;
    @FXML
    private PasswordField passwordPasswordField;

    public void login() {
        String userName = userNameTextField.getText();
        String password = passwordPasswordField.getText();

        String hashedPassword = hashPassword(password);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(USER_INFO_FILE))){
            Optional <String> lineOptional = Optional.empty();

            boolean isUserFound = false;
            while ((lineOptional = Optional.ofNullable(bufferedReader.readLine())).isPresent()){
                String line = lineOptional.get();
                String[] userInfo = line.split(";");
                StringBuilder storedUserName = new StringBuilder();
                StringBuilder storedPassword = new StringBuilder();
                StringBuilder storedUserType = new StringBuilder();
                if (userInfo.length==3){
                     storedUserName.append(userInfo[0]);
                     storedPassword.append(userInfo[1]);
                     storedUserType.append(userInfo[2]);
                }
                if (userName.contentEquals(storedUserName.toString())){
                    if (hashedPassword.contentEquals(storedPassword.toString())) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Uspješna prijava!");
                        alert.setHeaderText("Uspješna prijava!");
                        alert.setContentText("Uspješno ste se prijavili!");
                        alert.showAndWait();
                        isUserFound = true;

                        userMode = storedUserType.toString();
                        userNameGlobal = storedUserName.toString();

                        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("welcomeScreen.fxml"));
                        Scene scene = null;
                        try {
                            scene = new Scene(fxmlLoader.load(), 700, 500);
                            scene.getStylesheets().add(getClass().getResource("mainScreen.css").toExternalForm());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        HelloApplication.getMainStage().setScene(scene);
                        HelloApplication.getMainStage().show();

                    } else if (!(hashedPassword.contentEquals(storedPassword.toString()))) {
                        logger.error("Neispravna lozinka!");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Greška!");
                        alert.setHeaderText("Neuspješna prijava!");
                        alert.setContentText("Neispravna lozinka!");
                        alert.showAndWait();
                        isUserFound = true;
                    }
                }

            }
            if (!isUserFound){
                logger.error("Neispravno korisničko ime!");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Greška!");
                alert.setHeaderText("Neuspješna prijava!");
                alert.setContentText("Korisnik s unesenim korisničkim imenom ne postoji, potrebna je registracija!");
                alert.showAndWait();
            }

        }catch (IOException e){
            logger.error("Greška prilikom dohvaćanja podataka o korisniku iz datoteke", e);
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
            logger.error("Pogreška prilikom hashiranja lozinke!", e);
            return null;
        }
    }

    public void showSignUpScreen(){
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