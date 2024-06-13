package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.exceptions.*;
import hr.java.waterUsageJavaFxApplication.model.*;
import hr.java.waterUsageJavaFxApplication.threads.SerializeChangesThread;
import hr.java.waterUsageJavaFxApplication.utils.DatabaseUtils;
import hr.java.waterUsageJavaFxApplication.utils.FileUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static hr.java.waterUsageJavaFxApplication.HelloApplication.logger;

public class AddHouseholdController {
    @FXML
    private TextField householdAddressTextField;
    @FXML
    private TextField householdTenantsIdsTextField;
    @FXML
    private TableView<Tenant> tenantsTableView;
    @FXML
    private TableColumn<Tenant, String> tenantIdTableColumn;
    @FXML
    private TableColumn<Tenant, String> tenantNameTableColumn;
    @FXML
    private TableColumn<Tenant, String> tenantTotalWaterUsedTableColumn;

    public void initialize(){
        tenantIdTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getId().toString());
            }
        });

        tenantNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getFullName());
            }
        });

        tenantTotalWaterUsedTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getTotalWaterUsed().toString());
            }
        });

        List<Tenant> tenantsNotInHousehold = DatabaseUtils.getTenantsNotInHousehold();
        ObservableList<Tenant> tenantObservableList = FXCollections.observableArrayList(tenantsNotInHousehold);
        tenantsTableView.setItems(tenantObservableList);

    }

    public void saveHousehold(){
        String address = householdAddressTextField.getText();
        String tenantsIdsString = householdTenantsIdsTextField.getText();

        if (address.isEmpty()){
            logger.error("Polje za unos adrese ne smije ostati prazno!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno spremanje kućanstva!");
            alert.setContentText("Polje za unos adrese ne smije ostati prazno!");
            alert.showAndWait();
        } else if (tenantsIdsString.isEmpty()) {
            logger.error("Polje za unos ID-eva stanara ne smije ostati prazno!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno spremanje kućanstva!");
            alert.setContentText("Polje za unos ID-eva stanara ne smije ostati prazno!");
            alert.showAndWait();
        }
        else {

            try {
                checkTenantsIdsInput(tenantsIdsString);

                List<Tenant> tenantsList = DatabaseUtils.getTenantsFromDataBase();
                List<Long> tenantIds = Arrays.stream(tenantsIdsString.split(","))
                        .map(Long::parseLong)
                        .toList();

                List<Tenant> tenantsToGoInHousehold = tenantsList.stream()
                        .filter(tenant -> tenantIds.contains(tenant.getId()))
                        .toList();
                checkForTenants(tenantsToGoInHousehold);

                BigDecimal totalHouseholdWaterUsage = tenantsToGoInHousehold.stream()
                        .map(Tenant::getTotalWaterUsed)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);


                Household newHousehold = new Household.Builder(0L)
                        .atAddress(address)
                        .tenantList(tenantsToGoInHousehold)
                        .totalHouseholdWaterUsage(totalHouseholdWaterUsage)
                        .build();
                newHousehold.setLeak(newHousehold.determineLeakingStatus());

                List<Household> householdList = DatabaseUtils.getHouseholdsFromDatabase();
                checkDuplicateHousehold(householdList, newHousehold);
                DatabaseUtils.saveHouseholdToDatabase(newHousehold, tenantsToGoInHousehold);

                String changeDescription = "Dodano novo kućanstvo: " + newHousehold.getAddress();
                LocalDateTime changeTime = LocalDateTime.now();
                Changes change = new Changes(changeDescription, changeTime, LoginController.userMode + " " + LoginController.userNameGlobal);
                List<Changes> changesList = FileUtils.deserializeChanges();
                changesList.add(change);

                SerializeChangesThread serializeChangesThread = new SerializeChangesThread(changesList);
                Thread starter = new Thread(serializeChangesThread);
                starter.start();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Uspješno spremanje!");
                alert.setHeaderText("Spremanje novog kućanstva je uspješno!");
                alert.setContentText("Kućanstvo " + address + " uspješno je spremljeno!");
                alert.showAndWait();

                List<Tenant> tenantsNotInHousehold = DatabaseUtils.getTenantsNotInHousehold();
                ObservableList<Tenant> tenantObservableList = FXCollections.observableArrayList(tenantsNotInHousehold);
                tenantsTableView.setItems(tenantObservableList);

            } catch (TenantsIdsStringException | DuplicateHouseholdException | NoTenantsException e){
                logger.error(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Greška!");
                alert.setHeaderText("Neuspješno spremanje kućanstva!");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }


        }

    }

    private void checkTenantsIdsInput(String tenantsIdsString){
        if (!tenantsIdsString.matches("[0-9,]+")){
            throw new TenantsIdsStringException("Pogrešan unos ID-eva stanara!");
        }
    }

    private void checkDuplicateHousehold(List<Household> householdList, Household householdToCheck) throws DuplicateHouseholdException {
        for (Household household : householdList){
            if (household.equals(householdToCheck)){
                throw new DuplicateHouseholdException("Kućanstvo već postoji!");
            }
        }
    }

    private void checkForTenants(List<Tenant> tenantsToGoInHousehold) throws NoTenantsException {
        if (tenantsToGoInHousehold.isEmpty()){
            throw new NoTenantsException("Nema stanara s unesenim ID-evima!");
        }
    }

}
