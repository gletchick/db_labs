package org.gletchick.db.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.gletchick.db.factory.ServiceFactory;
import org.gletchick.db.model.Client;
import org.gletchick.db.model.Ticket;
import org.gletchick.db.service.ClientService;
import org.gletchick.db.service.TicketService;
import org.gletchick.db.util.UserSession;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProfileController extends BaseController {

    @FXML private VBox authContainer;
    @FXML private VBox profileContainer;

    @FXML private Label headerLabel, statusLabel;
    @FXML private TextField loginField, firstNameField, surnameField, patronymicField, phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Label lblFirstName, lblSurname, lblPatronymic, lblPhone;
    @FXML private Button mainActionButton, secondaryModeButton;

    @FXML private Label userFullInfoLabel;
    @FXML private ListView<String> historyListView;

    private final ClientService clientService = ServiceFactory.getInstance().getClientService();
    private final TicketService ticketService = ServiceFactory.getInstance().getTicketService();

    private boolean isLoginMode = true;

    @FXML
    public void initialize() {
        updateUI();
    }

    private void updateUI() {
        boolean loggedIn = UserSession.getInstance().isLoggedIn();

        authContainer.setVisible(!loggedIn);
        authContainer.setManaged(!loggedIn);

        profileContainer.setVisible(loggedIn);
        profileContainer.setManaged(loggedIn);

        if (loggedIn) {
            setupProfilePage();
        } else {
            statusLabel.setText("");
        }
    }

    private void setupProfilePage() {
        Client user = UserSession.getInstance().getCurrentClient();

        String info = String.format(
                "Фамилия: %s\nИмя: %s\nОтчество: %s\nЛогин: %s\nТелефон: %s",
                user.getSurname(),
                user.getName(),
                user.getPatronymic() != null ? user.getPatronymic() : "-",
                user.getLogin(),
                user.getPhone() != null ? user.getPhone() : "-"
        );

        if (userFullInfoLabel != null) {
            userFullInfoLabel.setText(info);
        }

        loadTicketHistory(user);
    }

    private void loadTicketHistory(Client user) {
        try {
            List<Ticket> allTickets = ticketService.findAll();

            List<String> userHistory = allTickets.stream()
                    .filter(t -> t.getClient() != null && t.getClient().getId().equals(user.getId()))
                    .map(t -> String.format("%s\nДата: %s | Статус: %s",
                            t.getSession().getSpectacle().getTitle(),
                            t.getSession().getDateTimeStart().toLocalDate().toString(),
                            t.getStatus()))
                    .collect(Collectors.toList());

            if (userHistory.isEmpty()) {
                historyListView.setItems(FXCollections.observableArrayList("История заказов пуста"));
            } else {
                historyListView.setItems(FXCollections.observableArrayList(userHistory));
            }
        } catch (Exception e) {
            historyListView.setItems(FXCollections.observableArrayList("Ошибка загрузки истории"));
            e.printStackTrace();
        }
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
            updateUI();
        } else {
            showError("Неверный логин или пароль");
        }
    }

    private void handleRegister() {
        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty() || firstNameField.getText().isEmpty()) {
            showError("Заполните обязательные поля!");
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
            updateUI();
        } catch (Exception e) {
            showError("Ошибка регистрации");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        UserSession.getInstance().logout();
        updateUI();
    }

    @FXML
    private void switchMode() {
        isLoginMode = !isLoginMode;
        headerLabel.setText(isLoginMode ? "Вход в систему" : "Регистрация");
        mainActionButton.setText(isLoginMode ? "Войти" : "Зарегистрироваться");
        secondaryModeButton.setText(isLoginMode ? "Создать аккаунт" : "Уже есть аккаунт?");

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

    private void showError(String msg) {
        statusLabel.setText(msg);
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
}