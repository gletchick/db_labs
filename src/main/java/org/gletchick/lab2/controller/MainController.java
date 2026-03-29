package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.*;
import org.gletchick.lab2.ui.ClientEditorController;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private static final String CLIENT_EDITOR_FXML = "/org/gletchick/lab2/ui/ClientEditor.fxml";
    private static final String TICKET_LIST_FXML = "/org/gletchick/lab2/ui/TicketListView.fxml";

    private static final String EDITOR_TITLE = "Редактор клиентов";
    private static final String STATUS_SOLD = "Куплен";
    private static final String STATUS_RESERVED = "Забронирован";
    private static final int DEFAULT_ID = 0;

    @FXML
    private ComboBox<Client> clientComboBox;
    @FXML
    private ComboBox<Session> sessionComboBox;
    @FXML
    private ComboBox<Seat> seatComboBox;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextField priceField;
    @FXML
    private Button openEditorButton;
    @FXML
    private Button buyTicketButton;

    private final DbManager dbManager = new DbManager();
    private final ObservableList<Client> clientsList = FXCollections.observableArrayList();
    private final ObservableList<Session> sessionsList = FXCollections.observableArrayList();
    private final ObservableList<Seat> seatsList = FXCollections.observableArrayList();


    @FXML
    private void handleShowAllTickets() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(TICKET_LIST_FXML));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Список всех билетов");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        setupComboBoxConverters();

        clientComboBox.setItems(clientsList);
        sessionComboBox.setItems(sessionsList);
        seatComboBox.setItems(seatsList);
        statusComboBox.setItems(FXCollections.observableArrayList(STATUS_SOLD, STATUS_RESERVED));

        loadClients();
        loadSessions();

        openEditorButton.setOnAction(event -> handleOpenEditor());
        buyTicketButton.setOnAction(event -> handleBuyTicket());

        // Слушатель: при смене сеанса меняем список мест под нужный зал
        sessionComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadSeatsForHall(newVal.getIdHall());
            } else {
                seatsList.clear();
            }
        });
    }

    private void loadClients() {
        try {
            clientsList.setAll(dbManager.readTableClients());
        } catch (SQLException e) {
            showError("Ошибка загрузки клиентов", e.getMessage());
        }
    }

    private void loadSessions() {
        try {
            sessionsList.setAll(dbManager.readTableSessions());
        } catch (SQLException e) {
            showError("Ошибка загрузки сеансов", e.getMessage());
        }
    }

    private void loadSeatsForHall(int hallId) {
        try {
            seatsList.setAll(dbManager.readSeatsByHall(hallId));
        } catch (SQLException e) {
            showError("Ошибка загрузки мест", e.getMessage());
        }
    }

    private void handleBuyTicket() {
        Client client = clientComboBox.getValue();
        Session session = sessionComboBox.getValue();
        Seat seat = seatComboBox.getValue();
        String status = statusComboBox.getValue();
        String priceText = priceField.getText();

        if (client == null || session == null || seat == null || status == null || priceText.isEmpty()) {
            showError("Заполните поля", "Пожалуйста, выберите все параметры билета.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            Ticket newTicket = new Ticket(
                    DEFAULT_ID,
                    session.getIdSession(),
                    seat.getIdSeat(),
                    client.getId(),
                    price,
                    status
            );

            EntityState<Ticket> ticketState = new EntityState<>(newTicket, RowState.ADDED);
            List<EntityState<Ticket>> ticketStates = List.of(ticketState);

            dbManager.syncAll(
                    new ArrayList<>(),
                    ticketStates,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>(),
                    new ArrayList<>()
            );

            showSuccess("Успех", "Билет успешно оформлен!");
            clearTicketFields();

        } catch (NumberFormatException e) {
            showError("Ошибка формата", "Введена некорректная цена.");
        } catch (SQLException e) {
            showError("Ошибка БД", e.getMessage());
        }
    }

    private void handleOpenEditor() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CLIENT_EDITOR_FXML));
            Parent root = loader.load();

            ClientEditorController controller = loader.getController();
            controller.setDbManager(dbManager);

            Stage stage = new Stage();
            stage.setTitle(EDITOR_TITLE);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.showAndWait();
            loadClients();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearTicketFields() {
        sessionComboBox.getSelectionModel().clearSelection();
        seatComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        priceField.clear();
    }

    private void setupComboBoxConverters() {
        clientComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Client client) {
                return client == null ? "" : client.getSurname() + " " + client.getName() + " (" + client.getPhone() + ")";
            }
            @Override
            public Client fromString(String string) { return null; }
        });

        sessionComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Session session) {
                return session == null ? "" : "Сеанс #" + session.getIdSession() + " (Зал " + session.getIdHall() + ")";
            }
            @Override
            public Session fromString(String string) { return null; }
        });

        seatComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Seat seat) {
                return seat == null ? "" : "Ряд " + seat.getRowNumber() + ", Место " + seat.getSeatNumber();
            }
            @Override
            public Seat fromString(String string) { return null; }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}