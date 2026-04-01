package org.gletchick.db.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.gletchick.db.model.Client;
import org.gletchick.db.service.ClientService;
import org.gletchick.db.service.impl.ClientServiceImpl;
import org.gletchick.db.util.UserSession;

import java.util.Optional;

public class ProfileController extends BaseController {

    @FXML private Label headerLabel, statusLabel;
    @FXML private TextField loginField, firstNameField, surnameField, patronymicField, phoneField;
    @FXML private PasswordField passwordField;

    @FXML private Label lblFirstName, lblSurname, lblPatronymic, lblPhone;
    @FXML private Button mainActionButton, secondaryModeButton;

    private final ClientService clientService = new ClientServiceImpl();
    private boolean isLoginMode = true;

    @FXML
    private void switchMode() {
        isLoginMode = !isLoginMode;

        headerLabel.setText(isLoginMode ? "Вход в систему" : "Регистрация");
        mainActionButton.setText(isLoginMode ? "Войти" : "Зарегистрироваться");
        secondaryModeButton.setText(isLoginMode ? "Создать аккаунт" : "Уже есть аккаунт?");

        // Управляем видимостью полей регистрации
        toggleField(firstNameField, lblFirstName, !isLoginMode);
        toggleField(surnameField, lblSurname, !isLoginMode);
        toggleField(patronymicField, lblPatronymic, !isLoginMode);
        toggleField(phoneField, lblPhone, !isLoginMode);

        statusLabel.setText("");
    }

    private void toggleField(Control field, Label label, boolean show) {
        field.setVisible(show);
        field.setManaged(show);
        label.setVisible(show);
        label.setManaged(show);
    }

    @FXML
    private void handlePrimaryAction() {
        if (isLoginMode) {
            handleLogin();
        } else {
            handleRegister();
        }
    }

    private void handleLogin() {
        String login = loginField.getText().trim();
        String pass = passwordField.getText().trim();

        if (login.isEmpty() || pass.isEmpty()) {
            showError("Введите логин и пароль");
            return;
        }

        Optional<Client> client = clientService.authenticate(login, pass);
        if (client.isPresent()) {
            UserSession.getInstance().login(client.get());
            showSuccess("Добро пожаловать, " + client.get().getName() + "!");
        } else {
            showError("Неверный логин или пароль");
        }
    }

    private void handleRegister() {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty() || firstNameField.getText().isEmpty()) {
            showError("Заполните обязательные поля (Логин, Пароль, Имя)!");
            return;
        }

        Client client = new Client();
        client.setLogin(loginField.getText());
        client.setPassword(passwordField.getText());
        client.setName(firstNameField.getText());
        client.setSurname(surnameField.getText());
        client.setPatronymic(patronymicField.getText());
        client.setPhone(phoneField.getText());

        try {
            Client saved = clientService.save(client);
            UserSession.getInstance().login(saved);
            showSuccess("Регистрация успешна! Вы вошли.");
        } catch (Exception e) {
            showError("Ошибка: возможно, такой логин уже занят");
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }

    private void showSuccess(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
    }
}