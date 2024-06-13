package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.model.Tenant;
import hr.java.waterUsageJavaFxApplication.utils.DatabaseUtils;
import hr.java.waterUsageJavaFxApplication.utils.FileUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TenantsSearchController {
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
    private RadioButton showerDurationRadioButton;
    @FXML
    private RadioButton dishwasherDurationRadioButton;
    @FXML
    private RadioButton washingMachineDurationRadioButton;
    @FXML
    private RadioButton carWashDurationRadioButton;
    @FXML
    private RadioButton totalWaterUsedRadioButton;
    @FXML
    private ComboBox<String> sortOrderComboBox;

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

    List<String> sortOptions = List.of("Uzlazno", "Silazno");
    ObservableList<String> sortOptionsObservableList = FXCollections.observableArrayList(sortOptions);
    sortOrderComboBox.setItems(sortOptionsObservableList);

    }

    public void tenantSearch(){
        List<Tenant> tenantList = DatabaseUtils.getTenantsFromDataBase();

        List<Tenant> sortedTenantList = new ArrayList<>();
        if (showerDurationRadioButton.isSelected()) {
             sortedTenantList = tenantList.stream()
                    .sorted((tenant1, tenant2) -> tenant1.getShower().getDurationMinutes().compareTo(tenant2.getShower().getDurationMinutes()))
                    .toList();
        } else if (dishwasherDurationRadioButton.isSelected()) {
            sortedTenantList = tenantList.stream()
                    .sorted((tenant1, tenant2) -> tenant1.getDishwasher().getDurationMinutes().compareTo(tenant2.getDishwasher().getDurationMinutes()))
                    .toList();
        } else if (washingMachineDurationRadioButton.isSelected()) {
            sortedTenantList = tenantList.stream()
                    .sorted((tenant1, tenant2) -> tenant1.getWashingMachine().getDurationMinutes().compareTo(tenant2.getWashingMachine().getDurationMinutes()))
                    .toList();
        } else if (carWashDurationRadioButton.isSelected()) {
            sortedTenantList = tenantList.stream()
                    .sorted((tenant1, tenant2) -> tenant1.getCarWash().getDurationMinutes().compareTo(tenant2.getCarWash().getDurationMinutes()))
                    .toList();
        } else if (totalWaterUsedRadioButton.isSelected()) {
            sortedTenantList = tenantList.stream()
                    .sorted((tenant1, tenant2) -> tenant1.getTotalWaterUsed().compareTo(tenant2.getTotalWaterUsed()))
                    .toList();
        } else {
            sortedTenantList = tenantList;

        }

        Optional<String> sortOrderOptional = Optional.ofNullable(sortOrderComboBox.getValue());
        ObservableList<Tenant> tenantObservableList;
        if (sortOrderOptional.isPresent() &&  sortOrderOptional.get().equals("Uzlazno")){
            tenantObservableList = FXCollections.observableArrayList(sortedTenantList);
        } else if (sortOrderOptional.isPresent() && sortOrderOptional.get().equals("Silazno")){
            tenantObservableList = FXCollections.observableArrayList(sortedTenantList);
            FXCollections.reverse(tenantObservableList);
        } else {
            tenantObservableList = FXCollections.observableArrayList(sortedTenantList);
        }

        tenantsTableView.setItems(tenantObservableList);
    }


}
