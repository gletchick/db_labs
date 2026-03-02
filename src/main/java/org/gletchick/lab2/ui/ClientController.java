package org.gletchick.lab2.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.db.Client;
import org.gletchick.lab2.db.ClientRepository;
import org.gletchick.lab2.db.DbManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> colSurname, colName, colPatronymic, colPhone;

    @FXML private TextField txtSurname, txtName, txtPatronymic, txtPhone;
    private ObservableList<Client> manualClients = FXCollections.observableArrayList();
    private final DbManager dbManager = new DbManager();

    private ObservableList<Client> clientsInMemory = FXCollections.observableArrayList();
    private List<Client> pendingInserts = new ArrayList<>();
    private final ClientRepository repository = new ClientRepository();

    @FXML
    public void initialize() {
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPatronymic.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        clientTable.setItems(repository.getAllClients());
    }

    @FXML
    private void handleAddLocal() {
        Client c = new Client(txtPhone.getText(), txtName.getText(), txtSurname.getText(), txtPatronymic.getText());
        repository.addLocally(c);

        txtPhone.clear(); txtName.clear(); txtSurname.clear(); txtPatronymic.clear();
        showInfo("Локальное добавление", "Запись добавлена в память. Не забудьте сохранить в БД!");
    }

    @FXML
    private void handleSaveToDb() {
        try {
            repository.saveChanges();
            showInfo("Успех", "Данные сохранены в БД");
        } catch (SQLException e) {
            showError("Ошибка", e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            repository.loadFromDb();
        } catch (SQLException e) {
            showError("Ошибка", e.getMessage());
        }
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}