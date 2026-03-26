package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import org.gletchick.lab2.model.Hall;
import org.gletchick.lab2.model.Seat;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeatController extends AbstractTableController<Seat> {

    @FXML
    private ListView<Hall> listHalls; // Добавьте этот элемент в FXML (fx:id="listHalls")

    @Override
    public void initialize() {
        super.initialize();
        loadHallsToList();

        // Настраиваем слушатель выбора зала
        listHalls.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadSeatsByHall(newVal.getIdHall());
            }
        });
    }

    private void loadHallsToList() {
        try {
            List<Hall> halls = dbManager.readTableHalls();
            listHalls.setItems(FXCollections.observableArrayList(halls));

            // Кастомизируем отображение в списке (чтобы видеть название, а не адрес объекта)
            listHalls.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Hall item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getHallName() + " (ID: " + item.getIdHall() + ")");
                    }
                }
            });
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить список залов", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    private void loadSeatsByHall(int hallId) {
        try {
            List<Seat> seats = dbManager.readSeatsByHall(hallId);
            states.clear();
            states.addAll(seats.stream()
                    .map(s -> new EntityState<>(s, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(seats));
            updateFooter(seats.size());
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка фильтрации мест", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected void setupColumns() {
        lblTitle.setText("Места в выбранном зале");
        tableView.setEditable(true);

        TableColumn<Seat, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idSeat"));

        TableColumn<Seat, Integer> colRow = new TableColumn<>("Ряд");
        colRow.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        colRow.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colRow.setOnEditCommit(e -> {
            e.getRowValue().setRowNumber(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        TableColumn<Seat, Integer> colSeatNum = new TableColumn<>("Место");
        colSeatNum.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        colSeatNum.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSeatNum.setOnEditCommit(e -> {
            e.getRowValue().setSeatNumber(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        tableView.getColumns().addAll(colId, colRow, colSeatNum);
    }

    @Override
    protected void loadData() {
        if (!listHalls.getItems().isEmpty()) {
            listHalls.getSelectionModel().selectFirst();
        }
    }

    @Override
    @FXML
    protected void saveChanges() {
        try {
            dbManager.syncAll(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(states), new ArrayList<>());
            // После сохранения обновляем текущий вид
            Hall selected = listHalls.getSelectionModel().getSelectedItem();
            if (selected != null) loadSeatsByHall(selected.getIdHall());
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка сохранения", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected Seat createEmptyEntity() {
        Hall selected = listHalls.getSelectionModel().getSelectedItem();
        int hallId = (selected != null) ? selected.getIdHall() : 1;
        return new Seat(0, hallId, 1, 1);
    }

    @FXML
    private void handleAdd() {
        addNewRow(createEmptyEntity());
    }

    @FXML
    private void handleDelete() {
        deleteSelected();
    }

    @Override
    @FXML
    protected void cancelChanges() {
        Hall selected = listHalls.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Если зал выбран, перезагружаем места только для него
            loadSeatsByHall(selected.getIdHall());
        } else {
            // Если ничего не выбрано, используем стандартное поведение
            super.cancelChanges();
        }
    }
}