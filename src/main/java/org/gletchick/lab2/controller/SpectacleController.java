package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import org.gletchick.lab2.model.Spectacle;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpectacleController extends AbstractTableController<Spectacle> {

    @Override
    protected void setupColumns() {
        lblTitle.setText("Спектакли");
        tableView.setEditable(true);

        // ID Спектакля
        TableColumn<Spectacle, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idSpectacle"));
        colId.setPrefWidth(50);

        // Название
        TableColumn<Spectacle, String> colTitle = new TableColumn<>("Название");
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colTitle.setCellFactory(TextFieldTableCell.forTableColumn());
        colTitle.setOnEditCommit(e -> {
            e.getRowValue().setTitle(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Жанр
        TableColumn<Spectacle, String> colGenre = new TableColumn<>("Жанр");
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colGenre.setCellFactory(TextFieldTableCell.forTableColumn());
        colGenre.setOnEditCommit(e -> {
            e.getRowValue().setGenre(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Продолжительность (мин)
        TableColumn<Spectacle, Integer> colDuration = new TableColumn<>("Длительность (мин)");
        colDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colDuration.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colDuration.setOnEditCommit(e -> {
            e.getRowValue().setDuration(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        tableView.getColumns().addAll(colId, colTitle, colGenre, colDuration);
    }

    @Override
    protected void loadData() {
        try {
            List<Spectacle> spectacles = dbManager.readTableSpectacles();
            states.clear();
            states.addAll(spectacles.stream()
                    .map(s -> new EntityState<>(s, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(spectacles));
            updateFooter(spectacles.size());
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить спектакли", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    @FXML
    protected void saveChanges() {
        try {
            // Передаем список спектаклей на 3-ю позицию в syncAll
            dbManager.syncAll(
                    new ArrayList<>(), // clients
                    new ArrayList<>(), // tickets
                    new ArrayList<>(states), // spectacles
                    new ArrayList<>(), // halls
                    new ArrayList<>(), // seats
                    new ArrayList<>()  // sessions
            );
            loadData();
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка сохранения спектаклей", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected Spectacle createEmptyEntity() {
        return new Spectacle(0, "Новое название", "Жанр", 120);
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