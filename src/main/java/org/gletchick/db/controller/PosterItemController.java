package org.gletchick.db.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.gletchick.db.model.Spectacle;

import java.io.IOException;

public class PosterItemController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label directorLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label languageLabel;

    private Spectacle currentSpectacle;

    public void setData(Spectacle spectacle) {
        this.currentSpectacle = spectacle;

        titleLabel.setText(spectacle.getTitle());
        ageLabel.setText(spectacle.getAgeRestriction() != null ? spectacle.getAgeRestriction() : "");
        genreLabel.setText("Жанр: " + spectacle.getGenre());
        directorLabel.setText("Режиссер: " + (spectacle.getDirector() != null ? spectacle.getDirector() : "Не указан"));
        durationLabel.setText("Длительность: " + spectacle.getDuration() + " мин");
        languageLabel.setText("Язык: " + (spectacle.getLanguage() != null ? spectacle.getLanguage() : "Не указан"));
    }

    @FXML
    private void handleBuyTicket() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ticket_booking.fxml"));
            VBox bookingView = loader.load();

            BookingController bookingController = loader.getController();
            bookingController.setSpectacle(currentSpectacle);

            StackPane contentArea = (StackPane) titleLabel.getScene().lookup("#contentArea");
            contentArea.getChildren().setAll(bookingView);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}