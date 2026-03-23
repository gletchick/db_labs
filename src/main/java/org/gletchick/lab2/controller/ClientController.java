package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientController extends AbstractTableController<Client> {

    @Override
    protected void setupColumns() {
        lblTitle.setText("Список клиентов");
        tableView.setEditable(true);

        // ID (не редактируемое)
        TableColumn<Client, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        // Фамилия
        TableColumn<Client, String> colSurname = createEditableColumn("Фамилия", "surname");
        colSurname.setOnEditCommit(e -> {
            e.getRowValue().setSurname(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Имя
        TableColumn<Client, String> colName = createEditableColumn("Имя", "name");
        colName.setOnEditCommit(e -> {
            e.getRowValue().setName(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Отчество
        TableColumn<Client, String> colPatronymic = createEditableColumn("Отчество", "patronymic");
        colPatronymic.setOnEditCommit(e -> {
            e.getRowValue().setPatronymic(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Телефон
        TableColumn<Client, String> colPhone = createEditableColumn("Телефон", "phone");
        colPhone.setOnEditCommit(e -> {
            e.getRowValue().setPhone(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        tableView.getColumns().addAll(colId, colSurname, colName, colPatronymic, colPhone);
    }

    private TableColumn<Client, String> createEditableColumn(String title, String property) {
        TableColumn<Client, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setCellFactory(TextFieldTableCell.forTableColumn());
        return col;
    }

    @Override
    protected void loadData() {
        try {
            List<Client> clients = dbManager.readTableClients();

            // Очищаем старые состояния и загружаем новые
            states.clear();
            states.addAll(clients.stream()
                    .map(c -> new EntityState<>(c, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(clients));
            updateFooter(clients.size());
        } catch (SQLException e) {
            showError("Ошибка загрузки данных", e.getMessage());
        }
    }

    @Override
    protected void saveChanges() {
        try {
            // Отправляем на синхронизацию текущий список состояний клиентов
            // Остальные списки передаем как пустые
            dbManager.syncAll(
                    new ArrayList<>(states),
                    new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>()
            );

            // После сохранения перечитываем данные, чтобы ID обновились и состояния сбросились в UNCHANGED
            loadData();
        } catch (SQLException e) {
            showError("Ошибка сохранения", e.getMessage());
        }
    }

    @Override
    protected Client createEmptyEntity() {
        // Создаем "пустого" клиента для новой строки
        return new Client(0, "", "Новый", "Клиент", "");
    }

    @FXML
    private void handleAdd() {
        addNewRow(createEmptyEntity());
    }

    @FXML
    private void handleDelete() {
        deleteSelected();
    }

    private void showError(String header, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}