package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.util.List;

public class Lab8ClientController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> colSurname;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colPhone;

    @FXML private TextField txtName, txtSurname, txtPhone;

    private DbManager dbManager;
    private Lab8MainController mainController;

    @FXML
    public void initialize() {
        dbManager = new DbManager();

        // Связываем колонки с полями класса Client (должны быть геттеры getName, getSurname, getPhone)
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        refreshTable();
    }

    // Метод для добавления нового клиента, как того требует задание 1 [cite: 119]
    @FXML
    private void addNewClient() {
        // Создаем объект со статусом ADDED для синхронизации [cite: 25]
        Client newClient = new Client(0, txtPhone.getText(), txtName.getText(), txtSurname.getText(), "");

        try {
            EntityState<Client> state = new EntityState<>(newClient, RowState.ADDED);
            // Вызываем твой метод сохранения [cite: 42]
            dbManager.syncAll(List.of(state), List.of(), List.of(), List.of(), List.of(), List.of());

            refreshTable();
            clearFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleBtnYes() {
        // Обновляем список в главной форме перед закрытием [cite: 28, 60]
        if (mainController != null) {
            mainController.loadClients();
        }
        closeWindow();
    }

    @FXML
    public void handleBtnNo() {
        closeWindow();
    }

    private void refreshTable() {
        try {
            clientTable.setItems(FXCollections.observableArrayList(dbManager.readTableClients()));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void clearFields() {
        txtName.clear();
        txtSurname.clear();
        txtPhone.clear();
    }

    public void setMainController(Lab8MainController mainController) {
        this.mainController = mainController;
    }

    private void closeWindow() {
        clientTable.getScene().getWindow().hide();
    }
}