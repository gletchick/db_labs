package org.gletchick.lab2.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.gletchick.lab2.model.*;
import org.gletchick.lab2.repository.ClientRepository;

import java.sql.SQLException;
import java.util.List;

public class ClientController {
    @FXML private TableView<EntityState<Client>> clientTable;
    @FXML private TableColumn<EntityState<Client>, String> colSurname, colName, colPatronymic, colPhone, colState;
    @FXML private TextField txtSurname, txtName, txtPatronymic, txtPhone;
    @FXML private ListView<String> hierarchyListView;

    // Используем репозиторий клиента, который теперь умеет подтягивать и билеты
    private final ClientRepository repository = new ClientRepository();

    @FXML
    public void initialize() {
        // Настройка колонок таблицы
        colSurname.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getSurname()));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getName()));
        colPatronymic.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getPatronymic()));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getPhone()));
        colState.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getState().toString()));

        clientTable.setItems(repository.getClientStates());

        // При выборе клиента в таблице — заполняем текстовые поля для редактирования
        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillFields(newVal.getEntity());
            }
        });
    }

    // --- 1. Загрузка данных ---
    @FXML
    private void handleRefresh() {
        try {
            repository.loadFromDb();
            showInfo("Успех", "Данные синхронизированы с БД");
        } catch (SQLException e) {
            showError("Ошибка загрузки", e.getMessage());
        }
    }

    @FXML
    private void handleAddLocal() {
        Client c = new Client(0, txtPhone.getText(), txtName.getText(), txtSurname.getText(), txtPatronymic.getText());
        repository.addLocally(c);
        clearFields();
    }

    @FXML
    private void handleEditLocal() {
        EntityState<Client> selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Client c = selected.getEntity();
            c.setSurname(txtSurname.getText());
            c.setName(txtName.getText());
            c.setPatronymic(txtPatronymic.getText());
            c.setPhone(txtPhone.getText());

            if (selected.getState() == RowState.UNCHANGED) {
                selected.setState(RowState.MODIFIED);
            }
            clientTable.refresh();
        }
    }

    @FXML
    private void handleDeleteLocal() {
        EntityState<Client> selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            repository.markDeleted(selected);
            clientTable.refresh();
        }
    }

    @FXML
    private void handleCancelEdit() {
        EntityState<Client> selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            repository.rejectChanges(selected);
            clientTable.refresh();
            fillFields(selected.getEntity());
        }
    }

    // --- 3. Иерархическое отображение (Master-Detail) ---
    @FXML
    private void handleShowHierarchy() {
        ObservableList<String> items = FXCollections.observableArrayList();

        for (EntityState<Client> clientState : repository.getClientStates()) {
            if (clientState.getState() == RowState.DELETED) continue;

            Client c = clientState.getEntity();
            items.add("КЛИЕНТ: " + c.getSurname() + " " + c.getName() + " [ID: " + c.getId() + "]");

            // ИСПОЛЬЗУЕМ РЕАЛЬНЫЙ ID
            int clientId = c.getId();

            List<Ticket> clientTickets = repository.getTicketsByClientId(clientId);

            if (clientTickets.isEmpty()) {
                items.add("   └─ (Билеты не найдены)");
            } else {
                for (Ticket t : clientTickets) {
                    items.add("   └─ Билет #" + t.getId() + ": Сеанс " + t.getIdSession() +
                            " | Место: " + t.getIdSeat() + " | Статус: " + t.getStatus());
                }
            }
        }
        hierarchyListView.setItems(items);
    }

    // --- 4. Сохранение всех изменений в БД ---
    @FXML
    private void handleSaveToDb() {
        try {
            repository.saveChanges();
            clientTable.refresh(); // Гарантируем, что статусы в колонке State обновятся на UNCHANGED
            showInfo("Успех", "Все изменения зафиксированы в БД. Новые ID получены.");
        } catch (SQLException e) {
            showError("Ошибка сохранения", e.getMessage());
        }
    }

    // Вспомогательные методы
    private void fillFields(Client c) {
        txtSurname.setText(c.getSurname());
        txtName.setText(c.getName());
        txtPatronymic.setText(c.getPatronymic());
        txtPhone.setText(c.getPhone());
    }

    private void clearFields() {
        txtSurname.clear(); txtName.clear(); txtPatronymic.clear(); txtPhone.clear();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}