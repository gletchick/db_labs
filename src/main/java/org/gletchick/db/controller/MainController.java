package org.gletchick.db.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    private void showPoster() {
        loadView("poster_list.fxml");
    }

    @FXML
    private void showMyTickets() {
        System.out.println("Открываем билеты...");
    }

    @FXML
    private void showAdminPanel() {
        loadView("admin_panel.fxml");
    }

    @FXML
    private void showProfile() {
        loadView("profile_view.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + fxmlFile));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}