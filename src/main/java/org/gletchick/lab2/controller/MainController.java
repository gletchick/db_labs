package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.context.TicketSystemContext;
import org.gletchick.lab2.model.Ticket;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainController {

    @FXML private TableView<Ticket> ticketsTable;
    @FXML private TableColumn<Ticket, Integer> colId;
    @FXML private TableColumn<Ticket, Integer> colSession;
    @FXML private TableColumn<Ticket, Integer> colSeat;
    @FXML private TableColumn<Ticket, Integer> colClient;
    @FXML private TableColumn<Ticket, Double> colPrice;
    @FXML private TableColumn<Ticket, String> colStatus;

    @FXML private TextField fldSession;
    @FXML private TextField fldSeat;
    @FXML private TextField fldClient;
    @FXML private TextField fldPrice;
    @FXML private TextField fldStatus;
    @FXML private TextField fldFilterPrice;
    @FXML private TextArea txtAreaOutput;

    private TicketSystemContext context;
    private ObservableList<Ticket> observableTickets;

    private static final String DEFAULT_NEW_TICKET_STATUS = "AVAILABLE";
    private static final int ID_PLACEHOLDER = 0;

    @FXML
    public void initialize() {
        context = new TicketSystemContext();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colSession.setCellValueFactory(new PropertyValueFactory<>("idSession"));
        colSeat.setCellValueFactory(new PropertyValueFactory<>("idSeat"));
        colClient.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        refreshTableData();

        ticketsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fldSession.setText(String.valueOf(newSelection.getIdSession()));
                fldSeat.setText(String.valueOf(newSelection.getIdSeat()));
                fldClient.setText(String.valueOf(newSelection.getIdClient()));
                fldPrice.setText(String.valueOf(newSelection.getPrice()));
                fldStatus.setText(newSelection.getStatus());
            }
        });
    }

    private void refreshTableData() {
        List<Ticket> activeTickets = context.getTickets();
        observableTickets = FXCollections.observableArrayList(activeTickets);
        ticketsTable.setItems(observableTickets);
    }

    @FXML
    private void handleAddTicket() {
        int session = Integer.parseInt(fldSession.getText());
        int seat = Integer.parseInt(fldSeat.getText());
        int client = Integer.parseInt(fldClient.getText());
        double price = Double.parseDouble(fldPrice.getText());
        String status = fldStatus.getText().isEmpty() ? DEFAULT_NEW_TICKET_STATUS : fldStatus.getText();

        Ticket newTicket = new Ticket(ID_PLACEHOLDER, session, seat, client, price, status);
        context.insertTicket(newTicket);
        refreshTableData();
    }

    @FXML
    private void handleUpdateTicket() {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setIdSession(Integer.parseInt(fldSession.getText()));
            selected.setIdSeat(Integer.parseInt(fldSeat.getText()));
            selected.setIdClient(Integer.parseInt(fldClient.getText()));
            selected.setPrice(Double.parseDouble(fldPrice.getText()));
            selected.setStatus(fldStatus.getText());

            context.markTicketModified(selected);
            refreshTableData();
        }
    }

    @FXML
    private void handleDeleteTicket() {
        Ticket selected = ticketsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            context.deleteTicket(selected);
            refreshTableData();
        }
    }

    @FXML
    private void handleSubmitChanges() {
        try {
            context.submitChanges();
            refreshTableData();
            txtAreaOutput.setText("Changes successfully saved to database.");
        } catch (SQLException e) {
            txtAreaOutput.setText("Error saving to DB: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilterTickets() {
        double minPrice = fldFilterPrice.getText().isEmpty() ? 0 : Double.parseDouble(fldFilterPrice.getText());

        List<Ticket> filteredAndSorted = context.getTickets().stream()
                .filter(t -> t.getPrice() > minPrice)
                .sorted((t1, t2) -> Double.compare(t2.getPrice(), t1.getPrice()))
                .collect(Collectors.toList());

        ticketsTable.setItems(FXCollections.observableArrayList(filteredAndSorted));
    }

    @FXML
    private void handleGroupTickets() {
        Map<String, Long> groupedData = context.getTickets().stream()
                .collect(Collectors.groupingBy(Ticket::getStatus, Collectors.counting()));

        StringBuilder sb = new StringBuilder("Tickets grouped by status:\n");
        groupedData.forEach((status, count) ->
                sb.append("Status: ").append(status).append(" | Count: ").append(count).append("\n"));

        txtAreaOutput.setText(sb.toString());
    }

    @FXML
    private void handleResetView() {
        refreshTableData();
        txtAreaOutput.clear();
    }
}