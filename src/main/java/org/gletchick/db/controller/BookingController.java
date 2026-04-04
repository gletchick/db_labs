package org.gletchick.db.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.gletchick.db.factory.ServiceFactory;
import org.gletchick.db.model.*;
import org.gletchick.db.service.*;
import org.gletchick.db.util.UserSession;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BookingController extends BaseController {

    @FXML private Label headerLabel;
    @FXML private ComboBox<Session> sessionComboBox;
    @FXML private FlowPane seatsPane;

    private final SessionService sessionService = ServiceFactory.getInstance().getSessionService();
    private final SeatService seatService = ServiceFactory.getInstance().getSeatService();
    private final TicketService ticketService = ServiceFactory.getInstance().getTicketService();

    private Spectacle selectedSpectacle;
    private Seat selectedSeat;
    private Button selectedSeatButton;

    public void setSpectacle(Spectacle spectacle) {
        this.selectedSpectacle = spectacle;
        headerLabel.setText("Бронирование: " + spectacle.getTitle());
        loadSessions();
    }

    @FXML
    private void handleSessionSelected() {
        Session selectedSession = sessionComboBox.getValue();
        if (selectedSession != null) {
            selectedSeat = null;
            selectedSeatButton = null;
            loadSeats(selectedSession);
        }
    }

    @FXML
    private void handleBuyTicket() {
        processTicket(TicketStatus.SOLD, "Билет успешно куплен!");
    }

    @FXML
    private void handleBookTicket() {
        processTicket(TicketStatus.BOOKED, "Билет успешно забронирован!");
    }

    // В loadSessions теперь используем фильтр БД
    private void loadSessions() {
        // Теперь запрос идет сразу с фильтром по ID спектакля
        List<Session> sessions = sessionService.findBySpectacle(selectedSpectacle.getIdSpectacle());
        sessionComboBox.getItems().setAll(sessions);
    }

    private void loadSeats(Session session) {
        seatsPane.getChildren().clear();

        // Получаем только места нужного зала
        List<Seat> allSeats = seatService.findByHall(session.getHall().getIdHall());

        // Получаем только ID занятых мест через БД
        Set<Integer> occupiedSeatIds = ticketService.findOccupiedSeatIdsBySession(session.getIdSession());

        for (Seat seat : allSeats) {
            Button seatButton = new Button("Р" + seat.getRowNumber() + " М" + seat.getSeatNumber());
            seatButton.setPrefSize(60, 40);

            if (occupiedSeatIds.contains(seat.getIdSeat())) {
                seatButton.setDisable(true);
                seatButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-opacity: 0.7;");
            } else {
                seatButton.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50;");
                seatButton.setOnAction(e -> {
                    selectedSeat = seat;
                    if (selectedSeatButton != null) {
                        selectedSeatButton.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50;");
                    }
                    selectedSeatButton = seatButton;
                    seatButton.setStyle("-fx-background-color: #1abc9c; -fx-text-fill: white;");
                });
            }
            seatsPane.getChildren().add(seatButton);
        }
    }

    private void processTicket(TicketStatus status, String successMessage) {
        if (sessionComboBox.getValue() == null || selectedSeat == null) {
            showAlert("Ошибка", "Выберите сеанс и свободное место!", Alert.AlertType.ERROR);
            return;
        }

        try {
            // Вызываем умный метод сервиса
            ticketService.processBooking(
                    sessionComboBox.getValue(),
                    selectedSeat,
                    UserSession.getInstance().getCurrentClient(),
                    status
            );

            showAlert("Успех", successMessage, Alert.AlertType.INFORMATION);
            handleSessionSelected(); // Обновляем карту мест

        } catch (IllegalStateException e) {
            // Если место перехватили
            showAlert("Ошибка", e.getMessage(), Alert.AlertType.ERROR);
            handleSessionSelected(); // Сразу обновляем карту, чтобы кнопка стала неактивной
        } catch (Exception e) {
            showAlert("Ошибка", "Произошла ошибка при сохранении", Alert.AlertType.ERROR);
        }
    }
}