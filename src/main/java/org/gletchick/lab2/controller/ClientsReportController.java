package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import net.sf.jasperreports.engine.JRException;
import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.service.ReportService;

import java.sql.SQLException;

public class ClientsReportController {

    @FXML
    private TableView<Client> clientsTable;
    @FXML
    private TableColumn<Client, Integer> colId;
    @FXML
    private TableColumn<Client, String> colSurname;
    @FXML
    private TableColumn<Client, String> colName;
    @FXML
    private TableColumn<Client, String> colPatronymic;
    @FXML
    private TableColumn<Client, String> colPhone;

    private final DbManager dbManager = new DbManager();
    private final ReportService reportService = new ReportService();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadClientsData();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPatronymic.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void loadClientsData() {
        try {
            ObservableList<Client> clients = FXCollections.observableArrayList(dbManager.readTableClients());
            clientsTable.setItems(clients);
        } catch (SQLException e) {
            showError("Ошибка БД", "Не удалось загрузить список клиентов: " + e.getMessage());
        }
    }

    @FXML
    private void handleGenerateReport() {
        Client selectedClient = clientsTable.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            showError("Клиент не выбран", "Пожалуйста, выберите клиента из таблицы для создания отчета.");
            return;
        }

        try {
            // Используем сервис для генерации
            reportService.generateClientCertificate(selectedClient);
        } catch (JRException e) {
            showError("Ошибка отчета", "Не удалось сгенерировать документ: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}