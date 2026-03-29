package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.model.Hall;
import org.gletchick.lab2.model.Seat;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class Lab8MainController {

    private static final String CLIENT_FORM_PATH = "/org/gletchick/lab2/ui/lab8_client.fxml";
    private static final String CLIENT_FORM_TITLE = "Справочник клиентов";
    private static final String ERROR_DB_LOAD = "DB Load Error";
    private static final String SEAT_FORMAT = "Ряд %d, Место %d";

    @FXML private ComboBox<Client> cbClient;
    @FXML private ComboBox<Hall> cbHall;
    @FXML private ComboBox<Seat> cbSeat;

    private DbManager dbManager;

    @FXML
    public void initialize() {
        dbManager = new DbManager();
        setupConverters();
        loadClients();
        loadHalls();
        setupFiltering();
    }

    private void setupConverters() {
        cbClient.setConverter(new StringConverter<>() {
            @Override
            public String toString(Client client) {
                return client == null ? "" : client.getSurname() + " " + client.getName();
            }
            @Override
            public Client fromString(String string) { return null; }
        });

        cbHall.setConverter(new StringConverter<>() {
            @Override
            public String toString(Hall hall) {
                return hall == null ? "" : hall.getHallName();
            }
            @Override
            public Hall fromString(String string) { return null; }
        });

        cbSeat.setConverter(new StringConverter<>() {
            @Override
            public String toString(Seat seat) {
                return seat == null ? "" : String.format(SEAT_FORMAT, seat.getRowNumber(), seat.getSeatNumber());
            }
            @Override
            public Seat fromString(String string) { return null; }
        });
    }

    public void loadClients() {
        try {
            List<Client> clients = dbManager.readTableClients();
            cbClient.setItems(FXCollections.observableArrayList(clients));
        } catch (SQLException e) {
            System.err.println(ERROR_DB_LOAD + ": " + e.getMessage());
        }
    }

    private void loadHalls() {
        try {
            List<Hall> halls = dbManager.readTableHalls();
            cbHall.setItems(FXCollections.observableArrayList(halls));
        } catch (SQLException e) {
            System.err.println(ERROR_DB_LOAD + ": " + e.getMessage());
        }
    }

    // Новый отдельный метод для загрузки мест (аналог того, что в методичке для должностей)
    // Добавь этот метод в Lab8MainController
    private void loadSeats(Hall hall) {
        if (hall != null) {
            try {
                List<Seat> seats = dbManager.readSeatsByHall(hall.getIdHall());
                cbSeat.setItems(FXCollections.observableArrayList(seats));
                if (!seats.isEmpty()) {
                    cbSeat.getSelectionModel().selectFirst();
                }
            } catch (SQLException e) {
                System.err.println("Error loading seats: " + e.getMessage());
            }
        }
    }

    // И обнови setupFiltering
    private void setupFiltering() {
        cbHall.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadSeats(newVal); // Вызываем метод при смене зала
        });
    }

    @FXML
    public void openClientDirectory(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(CLIENT_FORM_PATH));
            Parent root = loader.load();

            Lab8ClientController controller = loader.getController();
            controller.setMainController(this);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(CLIENT_FORM_TITLE);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(cbClient.getScene().getWindow());
            dialogStage.setScene(new Scene(root));

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}