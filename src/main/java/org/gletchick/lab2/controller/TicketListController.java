package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.Ticket;

import java.sql.SQLException;

public class TicketListController {
    private static final String COL_ID = "id";
    private static final String COL_SESSION = "idSession";
    private static final String COL_SEAT = "idSeat";
    private static final String COL_CLIENT = "idClient";
    private static final String COL_PRICE = "price";
    private static final String COL_STATUS = "status";

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, Integer> idColumn;
    @FXML
    private TableColumn<Ticket, Integer> sessionColumn;
    @FXML
    private TableColumn<Ticket, Integer> seatColumn;
    @FXML
    private TableColumn<Ticket, Integer> clientColumn;
    @FXML
    private TableColumn<Ticket, Double> priceColumn;
    @FXML
    private TableColumn<Ticket, String> statusColumn;

    private final DbManager dbManager = new DbManager();
    private final ObservableList<Ticket> ticketsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>(COL_ID));
        sessionColumn.setCellValueFactory(new PropertyValueFactory<>(COL_SESSION));
        seatColumn.setCellValueFactory(new PropertyValueFactory<>(COL_SEAT));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>(COL_CLIENT));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>(COL_PRICE));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(COL_STATUS));

        ticketsTable.setItems(ticketsList);
        loadTickets();
    }

    private void loadTickets() {
        try {
            ticketsList.setAll(dbManager.readTableTickets());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}