package org.gletchick.db.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.db.factory.ServiceFactory;
import org.gletchick.db.model.Ticket;
import org.gletchick.db.model.TicketStatus;
import org.gletchick.db.service.TicketService;
import org.gletchick.db.util.UserSession;

import java.util.List;

public class MyTicketsController extends BaseController {

    @FXML
    private TableView<Ticket> ticketsTable;
    @FXML
    private TableColumn<Ticket, Integer> idColumn;
    @FXML
    private TableColumn<Ticket, String> sessionColumn;
    @FXML
    private TableColumn<Ticket, Double> priceColumn;
    @FXML
    private TableColumn<Ticket, TicketStatus> statusColumn;

    private final TicketService ticketService = ServiceFactory.getInstance().getTicketService();

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sessionColumn.setCellValueFactory(new PropertyValueFactory<>("session"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadTickets();
    }

    private void loadTickets() {
        if (UserSession.getInstance().isLoggedIn()) {
            List<Ticket> userTickets = ticketService.findByClient(UserSession.getInstance().getCurrentClient());
            ObservableList<Ticket> observableList = FXCollections.observableArrayList(userTickets);
            ticketsTable.setItems(observableList);
        }
    }

    @FXML
    private void handleCancelTicket() {
        Ticket selectedTicket = ticketsTable.getSelectionModel().getSelectedItem();

        if (selectedTicket == null) {
            showAlert("Ошибка", "Выберите билет для отмены", Alert.AlertType.WARNING);
            return;
        }

        if (selectedTicket.getStatus() == TicketStatus.SOLD || selectedTicket.getStatus() == TicketStatus.BOOKED) {
            selectedTicket.setStatus(TicketStatus.AVAILABLE);
            selectedTicket.setClient(null);

            ticketService.update(selectedTicket);

            showAlert("Успех", "Билет успешно отменен и возвращен в продажу", Alert.AlertType.INFORMATION);
            loadTickets();
        } else {
            showAlert("Ошибка", "Можно отменить только купленные или забронированные билеты", Alert.AlertType.ERROR);
        }
    }
}