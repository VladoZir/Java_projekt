package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.exceptions.NotNumberException;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static hr.java.waterUsageJavaFxApplication.HelloApplication.logger;

public class UpdateTenantController {
    @FXML
    private TextField newTenantNameTextField;
    @FXML
    private TextField newShowerDurationTextField;
    @FXML
    private TextField newDishwasherDurationTextField;
    @FXML
    private TextField newWashingMachineDurationTextField;
    @FXML
    private TextField newCarWashDurationTextField;
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


    public void updateTenant() {
        Optional<Tenant> selectedTenantOptional = Optional.ofNullable(tenantComboBox.getValue());
        String newTenantName = newTenantNameTextField.getText();
        String newShowerDurationString = newShowerDurationTextField.getText();
        String newDishwasherDurationString = newDishwasherDurationTextField.getText();
        String newWashingMachineDurationString = newWashingMachineDurationTextField.getText();
        String newCarWashDurationString = newCarWashDurationTextField.getText();

        if (selectedTenantOptional.isEmpty()){
            logger.error("Nije odabran stanar za ažuriranje");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno ažuriranje stanara!");
            alert.setContentText("Nije odabran stanar!");
            alert.showAndWait();
        }else{
            Tenant selectedTenant = selectedTenantOptional.get();
            StringBuilder changeDescription = new StringBuilder();
            changeDescription.append("Napravljene promjene na stanaru ").append(selectedTenant.getFullName()).append(";");
            boolean isChanged = false;

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Potvrda ažuriranja");
            confirmationAlert.setHeaderText("Potvrda ažuriranja stanara");
            confirmationAlert.setContentText("Jeste li sigurni da želite ažurirati podatke stanara " + selectedTenant.getFullName() + "?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                List<Tenant> tenantList = DatabaseUtils.getTenantsFromDataBase();

                if (!(newTenantName.isEmpty())) {
                    String oldTenantName = selectedTenant.getFullName();
                    selectedTenant.setFullName(newTenantName);
                    changeDescription.append(" Ime stanara promijenjeno je iz ").append(oldTenantName).append(" u ")
                            .append(newTenantName).append(";");
                    isChanged = true;
                }
                if (!(newShowerDurationString.isEmpty())) {

                    try {
                        checkInputChar(newShowerDurationString);
                        BigDecimal newShowerDuration = new BigDecimal(newShowerDurationString);
                        BigDecimal oldShowerDuration = selectedTenant.getShower().getDurationMinutes();
                        selectedTenant.getShower().setDurationMinutes(newShowerDuration);
                        changeDescription.append(" Vrijeme tuširanja stanara promijenjeno je iz ").append(oldShowerDuration).append(" u ")
                                .append(newShowerDuration).append(" minuta;");
                        isChanged = true;
                    } catch (NotNumberException e) {
                        logger.error(e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Greška!");
                        alert.setHeaderText("Neuspješno ažuriranje stanara!");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                }
                if (!(newDishwasherDurationString.isEmpty())) {
                    try {
                        checkInputChar(newDishwasherDurationString);
                        BigDecimal newDishwasherDuration = new BigDecimal(newDishwasherDurationString);
                        BigDecimal oldDishwasherDuration = selectedTenant.getDishwasher().getDurationMinutes();
                        selectedTenant.getDishwasher().setDurationMinutes(newDishwasherDuration);
                        changeDescription.append(" Vrijeme pranja suđa stanara promijenjeno je iz ").append(oldDishwasherDuration).append(" u ")
                                .append(newDishwasherDuration).append(" minuta;");
                        isChanged = true;
                    } catch (NotNumberException e) {
                        logger.error(e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Greška!");
                        alert.setHeaderText("Neuspješno ažuriranje stanara!");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                }
                if (!(newWashingMachineDurationString.isEmpty())) {
                    try {
                        checkInputChar(newWashingMachineDurationString);
                        BigDecimal newWashingMachineDuration = new BigDecimal(newWashingMachineDurationString);
                        BigDecimal oldWashingMachineDuration = selectedTenant.getWashingMachine().getDurationMinutes();
                        selectedTenant.getWashingMachine().setDurationMinutes(newWashingMachineDuration);
                        changeDescription.append(" Vrijeme pranja rublja stanara promijenjeno je iz ").append(oldWashingMachineDuration).append(" u ")
                                .append(newWashingMachineDuration).append(" minuta;");
                        isChanged = true;
                    } catch (NotNumberException e) {
                        logger.error(e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Greška!");
                        alert.setHeaderText("Neuspješno ažuriranje stanara!");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                }
                if (!(newCarWashDurationString.isEmpty())) {
                    try {
                        checkInputChar(newCarWashDurationString);
                        BigDecimal newCarWashDuration = new BigDecimal(newCarWashDurationString);
                        BigDecimal oldCarWashDuration = selectedTenant.getCarWash().getDurationMinutes();
                        selectedTenant.getCarWash().setDurationMinutes(newCarWashDuration);
                        changeDescription.append(" Vrijeme pranja auta stanara promijenjeno je iz ").append(oldCarWashDuration).append(" u ")
                                .append(newCarWashDuration).append(" minuta;");
                        isChanged = true;
                    } catch (NotNumberException e) {
                        logger.error(e.getMessage());
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Greška!");
                        alert.setHeaderText("Neuspješno ažuriranje stanara!");
                        alert.setContentText(e.getMessage());
                        alert.showAndWait();
                    }
                }

                if (isChanged) {
                    DatabaseUtils.updateTenantInDatabase(selectedTenant);

                    LocalDateTime changeTime = LocalDateTime.now();

                    Changes change = new Changes(changeDescription.toString(), changeTime, LoginController.userMode + " " + LoginController.userNameGlobal);
                    List<Changes> changesList = FileUtils.deserializeChanges();
                    changesList.add(change);

                    SerializeChangesThread serializeChangesThread = new SerializeChangesThread(changesList);
                    Thread starter = new Thread(serializeChangesThread);
                    starter.start();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Uspješno ažuriranje!");
                    alert.setHeaderText("Ažuriranje stanara je uspješno!");
                    alert.setContentText("Podaci o stanaru " + selectedTenant.getFullName() + " uspješno su ažurirani!");
                    alert.showAndWait();
                }

            }else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ažuriranje otkazano!");
                alert.setHeaderText("Otkazali ste ažuriranje!");
                alert.setContentText("Podatci o stanaru " + selectedTenant.getFullName() + " nisu ažurirani!");
                alert.showAndWait();
            }

        }


        tenantsTableView.refresh();

    }

    private static void checkInputChar(String stringToCheck) throws NotNumberException {
        if (!stringToCheck.matches("[0-9]+")){
            throw new NotNumberException("Uneseni znak za vrijeme aktivnosti mora biti cijeli pozitivini broj! Pokušaj ponovno!");
        }
    }

}
