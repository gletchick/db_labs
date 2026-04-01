package org.gletchick.lab2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.model.Ticket;
import org.gletchick.lab2.repository.TicketRepositoryImpl;

import java.util.List;

public class TicketController {

    @FXML
    private TableView<Ticket> ticketTable;
    @FXML
    private TableColumn<Ticket, Integer> idCol;
    @FXML
    private TableColumn<Ticket, String> clientCol;
    @FXML
    private TableColumn<Ticket, String> spectacleCol;
    @FXML
    private TableColumn<Ticket, Double> priceCol;
    @FXML
    private TableColumn<Ticket, String> statusCol;

    private final TicketRepositoryImpl ticketRepository = new TicketRepositoryImpl();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Безопасный вывод ФИО клиента
        clientCol.setCellValueFactory(cellData -> {
            var c = cellData.getValue().getClient();
            if (c == null) return new SimpleStringProperty("Неизвестно");

            StringBuilder sb = new StringBuilder();

            // Фамилия
            if (c.getSurname() != null) {
                sb.append(c.getSurname());
            }

            // Имя (первая буква)
            if (c.getName() != null && !c.getName().isEmpty()) {
                sb.append(" ").append(c.getName().charAt(0)).append(".");
            }

            // Отчество (первая буква) - здесь падало чаще всего
            if (c.getPatronymic() != null && !c.getPatronymic().isEmpty()) {
                sb.append(c.getPatronymic().charAt(0)).append(".");
            }

            return new SimpleStringProperty(sb.toString().trim());
        });

        spectacleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSession().getSpectacle().getTitle())
        );

        loadData();
    }

    private void loadData() {
        List<Ticket> allTickets = ticketRepository.findAll();
        ticketTable.setItems(FXCollections.observableArrayList(allTickets));
    }
}