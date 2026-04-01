package org.gletchick.db.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputControl;

public abstract class BaseController {

    // Показать окно с сообщением
    protected void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected void clearFields(TextInputControl... fields) {
        for (TextInputControl field : fields) {
            field.clear();
        }
    }
}