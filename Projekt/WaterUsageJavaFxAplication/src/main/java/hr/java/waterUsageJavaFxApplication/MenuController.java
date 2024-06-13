package hr.java.waterUsageJavaFxApplication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;

import java.io.IOException;
import java.util.Objects;

public class MenuController {
    @FXML
    private MenuItem addTenantMenuItem;
    @FXML
    private MenuItem deleteTenantMenuItem;
    @FXML
    private MenuItem updateTenantMenuItem;
    @FXML
    private MenuItem addHouseholdMenuItem;
    @FXML
    private MenuItem deleteHouseholdMenuItem;
    @FXML
    private MenuItem updateHouseholdMenuItem;

    public void initialize() {
        if (Objects.equals(LoginController.userMode, "Gost")){
            addTenantMenuItem.setVisible(false);
            deleteTenantMenuItem.setVisible(false);
            updateTenantMenuItem.setVisible(false);
            addHouseholdMenuItem.setVisible(false);
            deleteHouseholdMenuItem.setVisible(false);
            updateHouseholdMenuItem.setVisible(false);
        }
    }
    public void showWelcomeScreen(){
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
    }
    public void showTenantsSearchScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("tenantsSearchScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 700, 600);
            scene.getStylesheets().add(getClass().getResource("mainScreen.css").toExternalForm());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }
    public void showAddTenantScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addTenantScreen.fxml"));
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

    public void showDeleteTenantScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("deleteTenant.fxml"));
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

    public void showUpdateTenantScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("updateTenant.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 700, 600);
            scene.getStylesheets().add(getClass().getResource("mainScreen.css").toExternalForm());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();

    }

    public void showHouseholdsSearchScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("householdsSearchScreen.fxml"));
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
    public void showAddHouseholdScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("addHouseholdScreen.fxml"));
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

    public void showDeleteHouseholdScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("deleteHousehold.fxml"));
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

    public void showUpdateHouseholdScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("updateHousehold.fxml"));
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

    public void showBillsSearchScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("billsSearchScreen.fxml"));
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

    public void showUpdatesScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("updatesScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 800, 500);
            scene.getStylesheets().add(getClass().getResource("mainScreen.css").toExternalForm());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HelloApplication.getMainStage().setScene(scene);
        HelloApplication.getMainStage().show();
    }

    public void showServiceScreen(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("serviceScreen.fxml"));
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

    public void logout(){
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
