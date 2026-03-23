package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.gletchick.lab2.model.Session;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SessionController extends AbstractTableController<Session> {

    // Формат для отображения и ввода даты в таблице
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    @Override
    protected void setupColumns() {
        lblTitle.setText("Сеансы");
        tableView.setEditable(true);

        // ID Сеанса
        TableColumn<Session, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idSession"));
        colId.setPrefWidth(50);

        // ID Спектакля
        TableColumn<Session, Integer> colSpecId = new TableColumn<>("ID Спектакля");
        colSpecId.setCellValueFactory(new PropertyValueFactory<>("idSpectacle"));
        colSpecId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSpecId.setOnEditCommit(e -> {
            e.getRowValue().setIdSpectacle(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // ID Зала
        TableColumn<Session, Integer> colHallId = new TableColumn<>("ID Зала");
        colHallId.setCellValueFactory(new PropertyValueFactory<>("idHall"));
        colHallId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colHallId.setOnEditCommit(e -> {
            e.getRowValue().setIdHall(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Дата и время начала
        TableColumn<Session, LocalDateTime> colDateTime = new TableColumn<>("Дата и время (" + DATE_TIME_FORMAT + ")");
        colDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTimeStart"));

        // Используем конвертер для LocalDateTime
        colDateTime.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateTimeStringConverter(formatter, null)));
        colDateTime.setOnEditCommit(e -> {
            e.getRowValue().setDateTimeStart(e.getNewValue());
            markAsModified(e.getRowValue());
        });
        colDateTime.setPrefWidth(200);

        tableView.getColumns().addAll(colId, colSpecId, colHallId, colDateTime);
    }

    @Override
    protected void loadData() {
        try {
            List<Session> sessions = dbManager.readTableSessions();
            states.clear();
            states.addAll(sessions.stream()
                    .map(s -> new EntityState<>(s, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(sessions));
            updateFooter(sessions.size());
        } catch (SQLException e) {
            showAlert("Ошибка", "Загрузка сеансов провалена", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    @FXML
    protected void saveChanges() {
        try {
            // Передаем список состояний сеансов на последнюю (6-ю) позицию
            dbManager.syncAll(
                    new ArrayList<>(), // clients
                    new ArrayList<>(), // tickets
                    new ArrayList<>(), // spectacles
                    new ArrayList<>(), // halls
                    new ArrayList<>(), // seats
                    new ArrayList<>(states) // sessions
            );
            loadData();
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось сохранить сеансы", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected Session createEmptyEntity() {
        // Создаем сеанс на текущее время, округленное до минут
        return new Session(0, 1, 1, LocalDateTime.now().withSecond(0).withNano(0));
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