package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.model.Changes;
import hr.java.waterUsageJavaFxApplication.model.Household;
import hr.java.waterUsageJavaFxApplication.model.Leak;
import hr.java.waterUsageJavaFxApplication.model.Tenant;
import hr.java.waterUsageJavaFxApplication.threads.SerializeChangesThread;
import hr.java.waterUsageJavaFxApplication.utils.DatabaseUtils;
import hr.java.waterUsageJavaFxApplication.utils.FileUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static hr.java.waterUsageJavaFxApplication.HelloApplication.logger;

public class DeleteHouseholdController {
    @FXML
    private TableView<Household> householdTableView;
    @FXML
    private TableColumn<Household, String> householdAddressTableColumn;
    @FXML
    private TableColumn<Household, String> householdTenantsTableColumn;
    @FXML
    private TableColumn<Household, String> householdTotalWaterUsageTableColumn;
    @FXML
    private TableColumn<Household, String> householdLeakingStatusTableColumn;
    @FXML
    private TableColumn<Household, String> householdTotalBillAmountTableColumn;
    @FXML
    private ComboBox<Household> householdComboBox;

    public void initialize(){
        householdAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Household, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Household, String> householdStringCellDataFeatures) {
                return new SimpleStringProperty(householdStringCellDataFeatures.getValue().getAddress());
            }
        });

        householdTenantsTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Household, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Household, String> householdStringCellDataFeatures) {
                List<Tenant> tenantList = householdStringCellDataFeatures.getValue().getTenantList();
                List<String> tenantNameList = tenantList.stream()
                        .map(Tenant::getFullName)
                        .toList();
                String tenantNameListString = tenantNameList.toString();
                tenantNameListString = tenantNameListString.substring(1, tenantNameListString.length() - 1);

                return new SimpleStringProperty(tenantNameListString);
            }
        });

        householdTotalWaterUsageTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Household, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Household, String> householdStringCellDataFeatures) {
                return new SimpleStringProperty(householdStringCellDataFeatures.getValue().getTotalHouseholdWaterUsage().toString());
            }
        });

        householdLeakingStatusTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Household, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Household, String> householdStringCellDataFeatures) {
                Leak leak = householdStringCellDataFeatures.getValue().getLeak();
                if(leak.isLeaking()){
                    return new SimpleStringProperty("Da");
                }
                else{
                    return new SimpleStringProperty("Ne");
                }
            }
        });

        householdTotalBillAmountTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Household, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Household, String> householdStringCellDataFeatures) {
                BigDecimal totalBill = householdStringCellDataFeatures.getValue().calculateTotalBill();
                BigDecimal totalBillRounded = totalBill.setScale(3, RoundingMode.HALF_UP);
                return new SimpleStringProperty(totalBillRounded.toString());
            }
        });

        List<Tenant> tenantList = DatabaseUtils.getTenantsFromDataBase();
        List<Household> householdList = DatabaseUtils.getHouseholdsFromDatabase();
        ObservableList<Household> householdObservableList = FXCollections.observableArrayList(householdList);
        householdComboBox.setItems(householdObservableList);
        householdTableView.setItems(householdObservableList);

    }


    public void deleteHousehold(){
        Optional<Household> householdOptional = Optional.ofNullable(householdComboBox.getValue());

        if(householdOptional.isEmpty()){
            logger.error("Nije odabrano kućanstvo!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno brisanje kućanstva!");
            alert.setContentText("Nije odabrano kućanstvo!");
            alert.showAndWait();
        }else{
            Household household = householdOptional.get();

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Potvrda brisanja");
            confirmationAlert.setHeaderText("Potvrda brisanja kućanstva");
            confirmationAlert.setContentText("Jeste li sigurni da želite obrisati kućanstvo " + household.getAddress() + "?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DatabaseUtils.deleteHouseholdFromDatabase(household);
                String changeDescription = "Obrisano kućanstvo: " + household.getAddress();
                LocalDateTime changeTime = LocalDateTime.now();
                Changes change = new Changes(changeDescription, changeTime, LoginController.userMode + " " + LoginController.userNameGlobal);
                List<Changes> changesList = FileUtils.deserializeChanges();
                changesList.add(change);

                SerializeChangesThread serializeChangesThread = new SerializeChangesThread(changesList);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(serializeChangesThread);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Uspješno brisanje!");
                alert.setHeaderText("Brisanje kućanstva je uspješno!");
                alert.setContentText("Kućanstvo " + household.getAddress() + " uspješno je obrisano!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Brisanje otkazano!");
                alert.setHeaderText("Otkazali ste brisanje!");
                alert.setContentText("Kućanstvo " + household.getAddress() + " nije obrisano!");
                alert.showAndWait();
            }

            List<Household> householdList = DatabaseUtils.getHouseholdsFromDatabase();
           ObservableList<Household> householdObservableList = FXCollections.observableArrayList(householdList);
            householdComboBox.setItems(householdObservableList);
            householdTableView.setItems(householdObservableList);

        }

    }

}
