package hr.java.waterUsageJavaFxApplication;

import hr.java.waterUsageJavaFxApplication.model.Changes;
import hr.java.waterUsageJavaFxApplication.utils.FileUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class UpdatesScreenController {
    @FXML
    private TableView<Changes> changesTableView;
    @FXML
    private TableColumn<Changes, String> changeDescriptionTableColumn;
    @FXML
    private TableColumn<Changes, String> dateTableColumn;
    @FXML
    private TableColumn<Changes, String> roleTableColumn;

    public void initialize() {
        changeDescriptionTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> param) {
                return new SimpleStringProperty(param.getValue().getChangeDescription());
            }
        });

        dateTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> param) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

                String formattedDateTime = param.getValue().getDate().format(formatter);


                return new SimpleStringProperty(formattedDateTime);
            }
        });

        roleTableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Changes, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Changes, String> param) {
                return new SimpleStringProperty(param.getValue().getRole());
            }
        });

        changeDescriptionTableColumn.setCellFactory(param -> {
            return new TableCell<Changes, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        Text text = new Text(item);
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                        setGraphic(text);
                    }
                }
            };
        });

        roleTableColumn.setCellFactory(param -> {
            return new TableCell<Changes, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        Text text = new Text(item);
                        text.setStyle("-fx-text-alignment:justify;");
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                        setGraphic(text);
                    }
                }
            };
        });

        List<Changes> changes = FileUtils.deserializeChanges();
        ObservableList<Changes> changesObservableList = FXCollections.observableArrayList(changes);
        changesTableView.setItems(changesObservableList);
        FileUtils.serializeChanges(changes);

    }

}
