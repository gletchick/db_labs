package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import org.gletchick.lab2.model.Seat;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeatController extends AbstractTableController<Seat> {

    @Override
    protected void setupColumns() {
        lblTitle.setText("Места в залах");
        tableView.setEditable(true);

        // ID Места (Auto-increment в БД)
        TableColumn<Seat, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idSeat"));
        colId.setPrefWidth(50);

        // ID Зала (Внешний ключ)
        TableColumn<Seat, Integer> colHallId = new TableColumn<>("ID Зала");
        colHallId.setCellValueFactory(new PropertyValueFactory<>("idHall"));
        colHallId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colHallId.setOnEditCommit(e -> {
            e.getRowValue().setIdHall(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Номер ряда
        TableColumn<Seat, Integer> colRow = new TableColumn<>("Ряд");
        colRow.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        colRow.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colRow.setOnEditCommit(e -> {
            e.getRowValue().setRowNumber(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Номер места
        TableColumn<Seat, Integer> colSeatNum = new TableColumn<>("Место");
        colSeatNum.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colSeatNum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSeatNum.setOnEditCommit(e -> {
            e.getRowValue().setSeatNumber(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        tableView.getColumns().addAll(colId, colHallId, colRow, colSeatNum);
    }

    @Override
    protected void loadData() {
        try {
            List<Seat> seats = dbManager.readTableSeats();
            states.clear();
            states.addAll(seats.stream()
                    .map(s -> new EntityState<>(s, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(seats));
            updateFooter(seats.size());
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить данные мест", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected void saveChanges() {
        try {
            // Передаем список состояний мест на 5-ю позицию (согласно сигнатуре syncAll)
            dbManager.syncAll(
                    new ArrayList<>(), // clients
                    new ArrayList<>(), // tickets
                    new ArrayList<>(), // spectacles
                    new ArrayList<>(), // halls
                    new ArrayList<>(states), // seats
                    new ArrayList<>()  // sessions
            );
            loadData();
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка при сохранении мест", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected Seat createEmptyEntity() {
        // Дефолтное место: ID=0 (для БД), HallID=1, Ряд=1, Место=1
        return new Seat(0, 1, 1, 1);
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