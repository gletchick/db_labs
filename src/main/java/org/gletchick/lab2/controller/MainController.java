package org.gletchick.lab2.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML private void showClients() { loadView("ClientView.fxml"); }
    @FXML private void showTickets() { loadView("TicketView.fxml"); }
    @FXML private void showSpectacles() { loadView("SpectacleView.fxml"); }
    @FXML private void showHalls() { loadView("HallView.fxml"); }
    @FXML private void showSeats() { loadView("SeatView.fxml"); }
    @FXML private void showSessions() { loadView("SessionView.fxml"); }

    private void loadView(String fxmlFile) {
        try {
            // Путь должен соответствовать расположению ваших FXML файлов
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/gletchick/lab2/ui/" + fxmlFile));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Не удалось загрузить: " + fxmlFile);
        }
    }
}