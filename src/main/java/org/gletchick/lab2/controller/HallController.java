package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import org.gletchick.lab2.model.Hall;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HallController extends AbstractTableController<Hall> {

    @Override
    protected void setupColumns() {
        lblTitle.setText("Залы");
        tableView.setEditable(true);

        // ID Зала
        TableColumn<Hall, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idHall"));
        colId.setPrefWidth(50);

        // Название зала
        TableColumn<Hall, String> colName = new TableColumn<>("Название");
        colName.setCellValueFactory(new PropertyValueFactory<>("hallName"));
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(e -> {
            e.getRowValue().setHallName(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Вместимость (с конвертером для чисел)
        TableColumn<Hall, Integer> colCapacity = new TableColumn<>("Вместимость");
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colCapacity.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colCapacity.setOnEditCommit(e -> {
            e.getRowValue().setCapacity(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        tableView.getColumns().addAll(colId, colName, colCapacity);
    }

    @Override
    protected void loadData() {
        try {
            List<Hall> halls = dbManager.readTableHalls();
            states.clear();
            states.addAll(halls.stream()
                    .map(h -> new EntityState<>(h, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(halls));
            updateFooter(halls.size());
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить данные", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected void saveChanges() {
        try {
            // Передаем список состояний залов, остальные списки пустые
            dbManager.syncAll(
                    new ArrayList<>(), // clients
                    new ArrayList<>(), // tickets
                    new ArrayList<>(), // spectacles
                    new ArrayList<>(states), // halls
                    new ArrayList<>(), // seats
                    new ArrayList<>()  // sessions
            );
            loadData(); // Обновляем UI после сохранения
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось сохранить изменения", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected Hall createEmptyEntity() {
        // ID оставляем 0, так как БД сама присвоит Serial ID
        return new Hall(0, "Новый зал", 0);
    }

    @FXML
    private void handleAdd() {
        addNewRow(createEmptyEntity());
    }

    @FXML
    private void handleDelete() {
        deleteSelected();
    }
}