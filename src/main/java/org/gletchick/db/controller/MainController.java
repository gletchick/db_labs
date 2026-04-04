package org.gletchick.db.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import org.gletchick.db.util.UserSession;

import java.io.IOException;

public class MainController extends BaseController{

    @FXML
    private StackPane contentArea;

    @FXML
    private void showPoster() {
        loadView("poster_list.fxml");
    }

    @FXML
    private void showMyTickets() {
        if (UserSession.getInstance().isLoggedIn()) {
            loadView("my_tickets.fxml");
        } else {
            showAlert("Внимание", "Пожалуйста, авторизуйтесь для просмотра билетов", Alert.AlertType.WARNING);
        }
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