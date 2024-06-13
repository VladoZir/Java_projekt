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
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static hr.java.waterUsageJavaFxApplication.HelloApplication.logger;

public class UpdateHouseholdController {
    @FXML
    private TextField newHouseholdAddressTextField;

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
    @FXML
    private ComboBox<Tenant> tenantsInHouseholdComboBox;
    @FXML
    private ComboBox<Tenant> tenantsNotInHouseholdComboBox;

    public void initialize(){
        householdAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Household, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Household, String> householdStringCellDataFeatures) {
                return new SimpleStringProperty(householdStringCellDataFeatures.getValue().getAddress());
            }
        });

        householdAddressTableColumn.setCellFactory(param -> {
            return new TableCell<Household, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        Text text = new Text(item);
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(55));
                        setGraphic(text);
                    }
                }
            };
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

        List<Household> householdList = DatabaseUtils.getHouseholdsFromDatabase();
        ObservableList<Household> householdObservableList = FXCollections.observableArrayList(householdList);
        householdComboBox.setItems(householdObservableList);
        householdTableView.setItems(householdObservableList);

        householdComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                ObservableList<Tenant> tenantsInHoseholdObservableList = FXCollections.observableArrayList(newValue.getTenantList());
                tenantsInHouseholdComboBox.setItems(tenantsInHoseholdObservableList);
            }
        });

        List<Tenant> tenantsNotInHouseholdList = DatabaseUtils.getTenantsNotInHousehold();
        ObservableList<Tenant> tenantsNotInHouseholdObservableList = FXCollections.observableArrayList(tenantsNotInHouseholdList);
        tenantsNotInHouseholdComboBox.setItems(tenantsNotInHouseholdObservableList);


    }

    public void updateHousehold() {
        Optional<Household> selectedHouseholdOptional = Optional.ofNullable(householdComboBox.getValue());
        String newHouseholdAddress = newHouseholdAddressTextField.getText();
        Optional<Tenant> tenantToRemoveOptional = Optional.ofNullable(tenantsInHouseholdComboBox.getValue());
        Optional<Tenant> tenantToAddOptional = Optional.ofNullable(tenantsNotInHouseholdComboBox.getValue());

        if (selectedHouseholdOptional.isEmpty()) {
            logger.error("Nije odabrano kućanstvo za ažuriranje");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno ažuriranje kućanstva!");
            alert.setContentText("Nije odabrano kućanstvo!");
            alert.showAndWait();
        } else {
            Household selectedHousehold = selectedHouseholdOptional.get();
            StringBuilder changeDescription = new StringBuilder();
            changeDescription.append("Napravljene promjene na kućanstvu ").append(selectedHousehold.getAddress()).append(":");
            boolean isChanged = false;

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Potvrda ažuriranja");
            confirmationAlert.setHeaderText("Potvrda ažuriranja kućanstva");
            confirmationAlert.setContentText("Jeste li sigurni da želite ažurirati podatke kućanstva " + selectedHousehold.getAddress() + "?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                //List<Tenant> tenantList = FileUtils.getTenantsFromFile();
                //List<Household> householdList = FileUtils.getHouseholdsFromFile(tenantList);
                List<Household> householdList = DatabaseUtils.getHouseholdsFromDatabase();

                if (!(newHouseholdAddress.isEmpty())) {
                    String oldHouseholdAddress = selectedHousehold.getAddress();
                    selectedHousehold.setAddress(newHouseholdAddress);
                    changeDescription.append(" Adresa je promijenjena iz ").append(oldHouseholdAddress).append(" u ")
                            .append(newHouseholdAddress).append(";");
                    isChanged = true;
                }
                if (tenantToRemoveOptional.isPresent()) {
                    Tenant tenantToRemove = tenantToRemoveOptional.get();
                    selectedHousehold.getTenantList().remove(tenantToRemove);
                    changeDescription.append(" Stanar ").append(tenantToRemove.getFullName()).append(" je uklonjen iz kućanstva;");
                    isChanged = true;
                }
                if (tenantToAddOptional.isPresent()) {
                    Tenant tenantToAdd = tenantToAddOptional.get();
                    selectedHousehold.getTenantList().add(tenantToAdd);
                    changeDescription.append(" Stanar ").append(tenantToAdd.getFullName()).append(" je dodan u kućanstvo;");
                    isChanged = true;
                }

                if (isChanged) {
                    List<Tenant> tenantsInHouseholdList = selectedHousehold.getTenantList();
                    tenantsInHouseholdComboBox.setItems(FXCollections.observableArrayList(tenantsInHouseholdList));

                    /*
                    for (Household household : householdList) {
                        if (household.getId().equals(selectedHousehold.getId())) {
                            household.setAddress(selectedHousehold.getAddress());
                            household.setTenantList(selectedHousehold.getTenantList());
                        }
                    }
                     FileUtils.saveHouseholdsToFile(householdList);
                     */

                    DatabaseUtils.updateHouseholdInDatabase(selectedHousehold);

                    LocalDateTime changeTime = LocalDateTime.now();

                    Changes change = new Changes(changeDescription.toString(), changeTime, LoginController.userMode + " " + LoginController.userNameGlobal);
                    List<Changes> changesList = FileUtils.deserializeChanges();
                    changesList.add(change);

                    SerializeChangesThread serializeChangesThread = new SerializeChangesThread(changesList);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(serializeChangesThread);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Uspješno ažuriranje!");
                    alert.setHeaderText("Ažuriranje kućanstva je uspješno!");
                    alert.setContentText("Podaci o kućanstvu " + selectedHousehold.getAddress() + " uspješno su ažurirani!");
                    alert.showAndWait();
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ažuriranje otkazano!");
                alert.setHeaderText("Otkazali ste ažuriranje!");
                alert.setContentText("Podatci o kućanstvu " + selectedHousehold.getAddress() + " nisu ažurirani!");
                alert.showAndWait();
            }

        }

        List<Household> householdList = DatabaseUtils.getHouseholdsFromDatabase();
        ObservableList<Household> householdObservableList = FXCollections.observableArrayList(householdList);
        householdTableView.setItems(householdObservableList);
        householdTableView.refresh();
        List<Tenant> tenantsNotInHoseholdList = DatabaseUtils.getTenantsNotInHousehold();
        ObservableList<Tenant> tenantsNotInHouseholdObservableList = FXCollections.observableArrayList(tenantsNotInHoseholdList);
        tenantsNotInHouseholdComboBox.setItems(tenantsNotInHouseholdObservableList);


    }

}