package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.model.Changes;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static hr.java.waterUsageJavaFxApplication.HelloApplication.logger;

public class DeleteTenantController {
    @FXML
    private TableView<Tenant> tenantsTableView;
    @FXML
    private TableColumn<Tenant, String> tenantNameTableColumn;
    @FXML
    private TableColumn<Tenant, String> tenantShowerDurationTableColumn;
    @FXML
    private TableColumn<Tenant, String> tenantDishwasherDurationTableColumn;
    @FXML
    private TableColumn<Tenant, String> tenantWashingMachineDurationTableColumn;
    @FXML
    private TableColumn<Tenant, String> tenantCarWashDurationTableColumn;
    @FXML
    private TableColumn<Tenant, String> tenantTotalWaterUsedTableColumn;
    @FXML
    private ComboBox<Tenant> tenantComboBox;

    public void initialize(){
        tenantNameTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getFullName());
            }
        });

        tenantShowerDurationTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getShower().getDurationMinutes().toString());
            }
        });

        tenantDishwasherDurationTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getDishwasher().getDurationMinutes().toString());
            }
        });

        tenantWashingMachineDurationTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getWashingMachine().getDurationMinutes().toString());
            }
        });

        tenantCarWashDurationTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getCarWash().getDurationMinutes().toString());
            }
        });

        tenantTotalWaterUsedTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Tenant, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Tenant, String> tenantStringCellDataFeatures) {
                return new SimpleStringProperty(tenantStringCellDataFeatures.getValue().getTotalWaterUsed().toString());
            }
        });

        List<Tenant> tenantList = DatabaseUtils.getTenantsFromDataBase();
        ObservableList<Tenant> tenantObservableList = FXCollections.observableArrayList(tenantList);
        tenantsTableView.setItems(tenantObservableList);
        tenantComboBox.setItems(tenantObservableList);

    }
    public void deleteTenant(){
        Optional<Tenant> tenantOptional = Optional.ofNullable(tenantComboBox.getValue());
        //List<Tenant> tenantList = FileUtils.getTenantsFromFile();

        if (tenantOptional.isEmpty()) {
            logger.error("Nije odabran stanar!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno brisanje stanara!");
            alert.setContentText("Nije odabran stanar!");
            alert.showAndWait();
        } else {
            Tenant tenant = tenantOptional.get();

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Potvrda brisanja");
            confirmationAlert.setHeaderText("Potvrda brisanja stanara");
            confirmationAlert.setContentText("Jeste li sigurni da želite obrisati stanara " + tenant.getFullName() + "?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                DatabaseUtils.deleteTenantFromDatabase(tenant);
                String changeDescription = "Obrisan stanar: " + tenant.getFullName();
                LocalDateTime changeTime = LocalDateTime.now();
                Changes change = new Changes(changeDescription, changeTime, LoginController.userMode + " " + LoginController.userNameGlobal);
                List<Changes> changesList = FileUtils.deserializeChanges();
                changesList.add(change);

                SerializeChangesThread serializeChangesThread = new SerializeChangesThread(changesList);
                Thread starter = new Thread(serializeChangesThread);
                starter.start();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Uspješno brisanje!");
                alert.setHeaderText("Brisanje stanara je uspješno!");
                alert.setContentText("Stanar " + tenant.getFullName() + " uspješno je obrisan!");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Brisanje otkazano!");
                alert.setHeaderText("Otkazali ste brisanje!");
                alert.setContentText("Stanar " + tenant.getFullName() + " nije obrisan!");
                alert.showAndWait();
            }

        }
        List<Tenant> tenantList = DatabaseUtils.getTenantsFromDataBase();
        ObservableList<Tenant> tenantObservableList = FXCollections.observableArrayList(tenantList);
        tenantComboBox.setItems(tenantObservableList);
        tenantsTableView.setItems(tenantObservableList);
        tenantsTableView.refresh();

    }

}
