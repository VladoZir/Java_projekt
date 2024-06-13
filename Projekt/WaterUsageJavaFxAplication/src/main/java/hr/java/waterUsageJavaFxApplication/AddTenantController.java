package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.exceptions.DuplicateTenantException;
import hr.java.waterUsageJavaFxApplication.exceptions.NotNumberException;
import hr.java.waterUsageJavaFxApplication.model.*;
import hr.java.waterUsageJavaFxApplication.threads.SerializeChangesThread;
import hr.java.waterUsageJavaFxApplication.utils.DatabaseUtils;
import hr.java.waterUsageJavaFxApplication.utils.FileUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static hr.java.waterUsageJavaFxApplication.HelloApplication.logger;

public class AddTenantController {
    @FXML
    private TextField tenantNameTextField;
    @FXML
    private TextField tenantShowerDurationTextField;
    @FXML
    private TextField tenantDishwasherDurationTextField;
    @FXML
    private TextField tenantWashingMachineDurationTextField;
    @FXML
    private TextField tenantCarWashDurationTextField;

    public void saveTenant(){
        String tenantName = tenantNameTextField.getText();
        String tenantShowerDurationString = tenantShowerDurationTextField.getText();
        String tenantDishwasherDurationString = tenantDishwasherDurationTextField.getText();
        String tenantWashingMachineDurationString = tenantWashingMachineDurationTextField.getText();
        String tenantCarWashDurationString = tenantCarWashDurationTextField.getText();

        if (tenantName.isEmpty()){
            logger.error("Polje za unos imena ne smije ostati prazno!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno spremanje stanara!");
            alert.setContentText("Polje za unos imena ne smije ostati prazno!");
            alert.showAndWait();
        } else if (tenantShowerDurationString.isEmpty()) {
            logger.error("Polje za unos trajanja tuširanja ne smije ostati prazno!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno spremanje stanara!");
            alert.setContentText("Polje za unos trajanja tuširanja ne smije ostati prazno!");
            alert.showAndWait();
        }else if (tenantDishwasherDurationString.isEmpty()) {
            logger.error("Polje za unos trajanja pranja suđa ne smije ostati prazno!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno spremanje stanara!");
            alert.setContentText("Polje za unos trajanja pranja suđa ne smije ostati prazno!");
            alert.showAndWait();
        } else if (tenantWashingMachineDurationString.isEmpty()) {
            logger.error("Polje za unos trajanja pranja rublja ne smije ostati prazno!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno spremanje stanara!");
            alert.setContentText("Polje za unos trajanja pranja rublja ne smije ostati prazno!");
            alert.showAndWait();
        } else if (tenantCarWashDurationString.isEmpty()) {
            logger.error("Polje za unos trajanja pranja automobila ne smije ostati prazno!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno spremanje stanara!");
            alert.setContentText("Polje za unos trajanja pranja automobila ne smije ostati prazno!");
            alert.showAndWait();
        } else {

            try {
                checkInputChar(tenantShowerDurationString);
                checkInputChar(tenantDishwasherDurationString);
                checkInputChar(tenantWashingMachineDurationString);
                checkInputChar(tenantCarWashDurationString);

                BigDecimal tenantShowerDuration = new BigDecimal(tenantShowerDurationString);
                BigDecimal tenantDishwasherDuration = new BigDecimal(tenantDishwasherDurationString);
                BigDecimal tenantWashingMachineDuration = new BigDecimal(tenantWashingMachineDurationString);
                BigDecimal tenantCarWashDuration = new BigDecimal(tenantCarWashDurationString);

                Shower shower = new Shower(tenantShowerDuration);
                Dishwasher dishwasher = new Dishwasher(tenantDishwasherDuration);
                WashingMachine washingMachine = new WashingMachine(tenantWashingMachineDuration);
                CarWash carWash = new CarWash(tenantCarWashDuration);

                Tenant newTenant = new Tenant(0L, tenantName, shower, dishwasher, washingMachine, carWash);

                List<Tenant> tenantList = DatabaseUtils.getTenantsFromDataBase();
                checkDuplicateTenant(tenantList, newTenant);
                DatabaseUtils.saveTenantToDatabase(newTenant);

                StringBuilder changeDescription = new StringBuilder("Dodan novi stanar: " + newTenant.getFullName());
                LocalDateTime changeTime = LocalDateTime.now();
                Changes change = new Changes(changeDescription.toString(), changeTime, LoginController.userMode + " " + LoginController.userNameGlobal);
                List<Changes> changesList = FileUtils.deserializeChanges();
                changesList.add(change);

                SerializeChangesThread serializeChangesThread = new SerializeChangesThread(changesList);
                Thread starter = new Thread(serializeChangesThread);
                starter.start();


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Uspješno spremanje!");
                alert.setHeaderText("Spremanje novog stanara je uspješno!");
                alert.setContentText("Stanar " + tenantName + " uspješno je spremljen!");
                alert.showAndWait();

            }catch (NotNumberException | DuplicateTenantException e){
                logger.error(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Greška!");
                alert.setHeaderText("Neuspješno spremanje stanara!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }


    }

    private static void checkInputChar(String stringToCheck) throws NotNumberException {
        if (!stringToCheck.matches("[0-9]+")){
            throw new NotNumberException("Uneseni znak za vrijeme aktivnosti mora biti cijeli pozitivini broj! Pokušaj ponovno!");
        }
    }

    private static void checkDuplicateTenant(List<Tenant> tenantList, Tenant tenantToCheck) {
        for (Tenant tenant : tenantList){
            if (tenant.equals(tenantToCheck)){
                throw new DuplicateTenantException("Stanar " + tenantToCheck + " već postoji! Pokušaj ponovno!");
            }
        }
    }

}
