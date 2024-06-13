package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.model.*;
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

public class ServiceController {
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

    public void initialize() {
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
                if (leak.isLeaking()) {
                    return new SimpleStringProperty("Da");
                } else {
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

        LeakingHouseholdGeneric<Household> leakingHouseholdGeneric = DatabaseUtils.getHouseholdsWithLeak();
        List<Household> householdWithLeakList = leakingHouseholdGeneric.getLeakingHouseholdList();
        ObservableList<Household> householdObservableList = FXCollections.observableArrayList(householdWithLeakList);
        householdTableView.setItems(householdObservableList);
        householdComboBox.setItems(householdObservableList);

    }

    public void serviceHousehold(){
        Optional<Household> householdOptional = Optional.ofNullable(householdComboBox.getValue());

        if(householdOptional.isPresent()){
            Household household = householdOptional.get();
            household.setLeak(new Leak(false));
            DatabaseUtils.updateHouseholdInDatabase(household);

            LocalDateTime changeTime = LocalDateTime.now();
            String changeDescription = "Kućanstvo " + household.getAddress() + " je uspješno servisirano!";
            Changes change = new Changes(changeDescription, changeTime, LoginController.userMode + " " + LoginController.userNameGlobal);
            List<Changes> changesList = FileUtils.deserializeChanges();
            changesList.add(change);
            SerializeChangesThread serializeChangesThread = new SerializeChangesThread(changesList);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(serializeChangesThread);

            logger.info("Kućanstvo " + household.getAddress() + " je uspješno servisirano!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informacija");
            alert.setHeaderText("Uspješno servisiranje kućanstva!");
            alert.setContentText("Kućanstvo " + household.getAddress() + " je uspješno servisirano!");
            alert.showAndWait();

        }else {
            logger.error("Nije odabran kućanstvo za servisiranje!");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Greška!");
            alert.setHeaderText("Neuspješno servisiranje kućanstva!");
            alert.setContentText("Potrebno je odabrati kućanstvo!");
            alert.showAndWait();
        }


        LeakingHouseholdGeneric<Household> leakingHouseholdGeneric = DatabaseUtils.getHouseholdsWithLeak();
        List<Household> householdWithLeakList = leakingHouseholdGeneric.getLeakingHouseholdList();
        ObservableList<Household> householdObservableList = FXCollections.observableArrayList(householdWithLeakList);
        householdTableView.setItems(householdObservableList);
        householdComboBox.setItems(householdObservableList);
        householdTableView.refresh();

    }

}
