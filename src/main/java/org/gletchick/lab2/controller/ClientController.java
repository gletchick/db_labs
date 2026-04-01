package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.repository.ClientRepositoryImpl;

import java.util.List;

public class ClientController {

    @FXML
    private TableView<Client> clientTable;
    @FXML
    private TableColumn<Client, Integer> idCol;
    @FXML
    private TableColumn<Client, String> phoneCol;
    @FXML
    private TableColumn<Client, String> nameCol;
    @FXML
    private TableColumn<Client, String> surnameCol;
    @FXML
    private TableColumn<Client, String> patronymicCol;

    private final ClientRepositoryImpl clientRepository = new ClientRepositoryImpl();

    @FXML
    public void initialize() {
        // Установка фабрик значений для столбцов таблицы
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));
        patronymicCol.setCellValueFactory(new PropertyValueFactory<>("patronymic"));

        loadData();
    }

    private void loadData() {
        List<Client> allClients = clientRepository.findAll();
        clientTable.setItems(FXCollections.observableArrayList(allClients));
    }
}