package org.gletchick.lab2.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientEditorController {
    private static final String COL_ID = "id";
    private static final String COL_PHONE = "phone";
    private static final String COL_NAME = "name";
    private static final String COL_SURNAME = "surname";
    private static final String COL_PATRONYMIC = "patronymic";
    private static final int DEFAULT_ID = 0;

    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, Integer> idColumn;
    @FXML
    private TableColumn<Client, String> phoneColumn;
    @FXML
    private TableColumn<Client, String> nameColumn;
    @FXML
    private TableColumn<Client, String> surnameColumn;
    @FXML
    private TableColumn<Client, String> patronymicColumn;

    @FXML
    private TextField phoneField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField patronymicField;
    @FXML
    private Button saveButton;

    private DbManager dbManager;
    private final ObservableList<Client> clientsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>(COL_ID));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>(COL_PHONE));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>(COL_NAME));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>(COL_SURNAME));
        patronymicColumn.setCellValueFactory(new PropertyValueFactory<>(COL_PATRONYMIC));

        clientsTable.setItems(clientsList);
        saveButton.setOnAction(event -> handleSave());
    }

    public void setDbManager(DbManager dbManager) {
        this.dbManager = dbManager;
        refreshTable();
    }

    private void refreshTable() {
        try {
            clientsList.setAll(dbManager.readTableClients());
        } catch (SQLException e) {
            showError("Ошибка загрузки данных", e.getMessage());
        }
    }

    private void handleSave() {
        Client newClient = new Client(
                DEFAULT_ID,
                phoneField.getText(),
                nameField.getText(),
                surnameField.getText(),
                patronymicField.getText()
        );

        EntityState<Client> state = new EntityState<>(newClient, RowState.ADDED);
        List<EntityState<Client>> clientStates = List.of(state);

        try {
            // Вызываем общую синхронизацию, передавая пустые списки для других сущностей
            dbManager.syncAll(
                    clientStates,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            clearFields();
            refreshTable();

        } catch (SQLException e) {
            showError("Ошибка сохранения", e.getMessage());
        }
    }

    private void clearFields() {
        phoneField.clear();
        nameField.clear();
        surnameField.clear();
        patronymicField.clear();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}