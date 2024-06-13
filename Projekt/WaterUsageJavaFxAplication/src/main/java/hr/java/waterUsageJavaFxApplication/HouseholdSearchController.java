package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.model.Household;
import hr.java.waterUsageJavaFxApplication.model.Leak;
import hr.java.waterUsageJavaFxApplication.model.Tenant;
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
import java.util.List;
import java.util.Optional;

public class HouseholdSearchController {
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
    private ComboBox<String> sortOrderComboBox;
    @FXML
    private ToggleButton hasLeakToggleButton;
    @FXML
    private ToggleButton noLeakToggleButton;
    @FXML
    private ComboBox<String> sortByComboBox;

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

        List<String> sortOptions = List.of("Uzlazno", "Silazno");
        ObservableList<String> sortOptionsObservableList = FXCollections.observableArrayList(sortOptions);
        sortOrderComboBox.setItems(sortOptionsObservableList);

        List<String> sortByOptions = List.of("Adresa", "Broj stanara", "Ukupna potrošnja vode", "Cijena računa");
        ObservableList<String> sortByOptionsObservableList = FXCollections.observableArrayList(sortByOptions);
        sortByComboBox.setItems(sortByOptionsObservableList);


    }

    public void householdSearch() {
        List<Household> householdList = DatabaseUtils.getHouseholdsFromDatabase();

        if (hasLeakToggleButton.isSelected()) {
            householdList = householdList.stream()
                    .filter(household -> household.getLeak().isLeaking())
                    .toList();
        } else if (noLeakToggleButton.isSelected()) {
            householdList = householdList.stream()
                    .filter(household -> !household.getLeak().isLeaking())
                    .toList();
        }

        Optional<String> sortByOptional = Optional.ofNullable(sortByComboBox.getValue());
        if (sortByOptional.isPresent() && sortByOptional.get().equals("Adresa")){
            householdList = householdList.stream()
                    .sorted((household1, household2) -> household1.getAddress().compareTo(household2.getAddress()))
                    .toList();
        } else if (sortByOptional.isPresent() && sortByOptional.get().equals("Broj stanara")){
            householdList = householdList.stream()
                    .sorted((household1, household2) -> household1.getTenantList().size() - household2.getTenantList().size())
                    .toList();
        } else if (sortByOptional.isPresent() && sortByOptional.get().equals("Ukupna potrošnja vode")){
            householdList = householdList.stream()
                    .sorted((household1, household2) -> household1.getTotalHouseholdWaterUsage().compareTo(household2.getTotalHouseholdWaterUsage()))
                    .toList();
        } else if (sortByOptional.isPresent() && sortByOptional.get().equals("Cijena računa")){
            householdList = householdList.stream()
                    .sorted((household1, household2) -> household1.calculateTotalBill().compareTo(household2.calculateTotalBill()))
                    .toList();
        }

        Optional<String> sortOrderOptional = Optional.ofNullable(sortOrderComboBox.getValue());
        ObservableList<Household> householdObservableList;
        if (sortOrderOptional.isPresent() &&  sortOrderOptional.get().equals("Uzlazno")){
            householdObservableList = FXCollections.observableArrayList(householdList);
        } else if (sortOrderOptional.isPresent() && sortOrderOptional.get().equals("Silazno")){
            householdObservableList = FXCollections.observableArrayList(householdList);
            FXCollections.reverse(householdObservableList);
        } else {
            householdObservableList = FXCollections.observableArrayList(householdList);
        }

        householdTableView.setItems(householdObservableList);
        householdTableView.refresh();

    }

}
