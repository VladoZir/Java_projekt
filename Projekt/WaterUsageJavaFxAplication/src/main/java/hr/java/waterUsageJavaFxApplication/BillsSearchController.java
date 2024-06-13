package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.model.Bill;
import hr.java.waterUsageJavaFxApplication.utils.DatabaseUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BillsSearchController {
    @FXML
    private TableView<Bill<BigDecimal>> billTableView;
    @FXML
    private TableColumn<Bill<BigDecimal>, String> billAddressTableColumn;
    @FXML
    private TableColumn<Bill<BigDecimal>, String> billTotalWaterUsedTableColumn;
    @FXML
    private TableColumn<Bill<BigDecimal>, String> billTotalBillTableColumn;
    @FXML
    private ComboBox<String> sortOrderComboBox;
    @FXML
    private ComboBox<String> sortByComboBox;

    public void initialize(){
        billAddressTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill<BigDecimal>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill<BigDecimal>, String> param) {
                return new SimpleStringProperty(param.getValue().getAddress());
            }
        });

        billTotalWaterUsedTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill<BigDecimal>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill<BigDecimal>, String> param) {
                return new SimpleStringProperty(param.getValue().getTotalWaterUsed().toString());
            }
        });

        billTotalBillTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Bill<BigDecimal>, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Bill<BigDecimal>, String> param) {
                BigDecimal totalBill = param.getValue().getTotalBill();
                BigDecimal totalBillRounded = totalBill.setScale(3, RoundingMode.HALF_UP);
                return new SimpleStringProperty(totalBillRounded.toString());
            }
        });

        List<String> sortOptions = List.of("Uzlazno", "Silazno");
        ObservableList<String> sortOptionsObservableList = FXCollections.observableArrayList(sortOptions);
        sortOrderComboBox.setItems(sortOptionsObservableList);

        List<String> sortByOptions = List.of("Adresa", "Cijena računa", "Ukupna potrošnja vode");
        ObservableList<String> sortByOptionsObservableList = FXCollections.observableArrayList(sortByOptions);
        sortByComboBox.setItems(sortByOptionsObservableList);

    }


    public void billsSearch() {
        Set<Bill<BigDecimal>> billSet = DatabaseUtils.getBillsFromDatabase();

        Map<String, Bill<BigDecimal>> billMap = new HashMap<>();
        for (Bill<BigDecimal> bill : billSet){
            billMap.put(bill.getAddress(), bill);
        }

        List<Bill<BigDecimal>> billList = new ArrayList<>(billMap.values());
        Optional<String> sortByOptional = Optional.ofNullable(sortByComboBox.getValue());
        if (sortByOptional.isPresent()){
            Optional<Comparator<Bill<BigDecimal>>> comparator = Optional.empty();
            if (sortByOptional.get().equals("Adresa")){
                comparator = Optional.of(Comparator.comparing(Bill::getAddress));
            }else if (sortByOptional.get().equals("Cijena računa")) {
                comparator = Optional.of(Comparator.comparing(Bill::getTotalBill));
            } else if (sortByOptional.get().equals("Ukupna potrošnja vode")) {
                comparator = Optional.of(Comparator.comparing(Bill::getTotalWaterUsed));
            }

            if (comparator.isPresent()) {
                Optional<String> sortOrderOptional = Optional.ofNullable(sortOrderComboBox.getValue());
                if (sortOrderOptional.isPresent() && sortOrderOptional.get().equals("Silazno")) {
                    comparator = Optional.of(comparator.get().reversed());
                }
                billList.sort(comparator.get());
            }

        }

        ObservableList<Bill<BigDecimal>> billsObservableList = FXCollections.observableArrayList(billList);

        billTableView.setItems(billsObservableList);

    }

}
