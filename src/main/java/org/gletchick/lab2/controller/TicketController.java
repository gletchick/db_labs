package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.gletchick.lab2.model.Ticket;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketController extends AbstractTableController<Ticket> {

    private static final String DEFAULT_STATUS = "AVAILABLE";
    private static final double DEFAULT_PRICE = 500.0;

    @Override
    protected void setupColumns() {
        lblTitle.setText("Билеты");
        tableView.setEditable(true);

        // ID Билета
        TableColumn<Ticket, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        // ID Сеанса
        TableColumn<Ticket, Integer> colSession = new TableColumn<>("ID Сеанса");
        colSession.setCellValueFactory(new PropertyValueFactory<>("idSession"));
        colSession.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSession.setOnEditCommit(e -> {
            e.getRowValue().setIdSession(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // ID Места
        TableColumn<Ticket, Integer> colSeat = new TableColumn<>("ID Места");
        colSeat.setCellValueFactory(new PropertyValueFactory<>("idSeat"));
        colSeat.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSeat.setOnEditCommit(e -> {
            e.getRowValue().setIdSeat(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // ID Клиента
        TableColumn<Ticket, Integer> colClient = new TableColumn<>("ID Клиента");
        colClient.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        colClient.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colClient.setOnEditCommit(e -> {
            e.getRowValue().setIdClient(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Цена
        TableColumn<Ticket, Double> colPrice = new TableColumn<>("Цена");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colPrice.setOnEditCommit(e -> {
            e.getRowValue().setPrice(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        // Статус
        TableColumn<Ticket, String> colStatus = new TableColumn<>("Статус");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(TextFieldTableCell.forTableColumn());
        colStatus.setOnEditCommit(e -> {
            e.getRowValue().setStatus(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        tableView.getColumns().addAll(colId, colSession, colSeat, colClient, colPrice, colStatus);
    }

    @Override
    protected void loadData() {
        try {
            List<Ticket> tickets = dbManager.readTableTickets();
            states.clear();
            states.addAll(tickets.stream()
                    .map(t -> new EntityState<>(t, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(tickets));
            updateFooter(tickets.size());
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить билеты", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    @FXML
    protected void saveChanges() {
        try {
            // Передаем список билетов на 2-ю позицию в syncAll
            dbManager.syncAll(
                    new ArrayList<>(), // clients
                    new ArrayList<>(states), // tickets
                    new ArrayList<>(), // spectacles
                    new ArrayList<>(), // halls
                    new ArrayList<>(), // seats
                    new ArrayList<>()  // sessions
            );
            loadData();
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка сохранения билетов", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected Ticket createEmptyEntity() {
        // Используем конструктор со всеми параметрами. ID=0 для БД.
        return new Ticket(0, 1, 1, 1, DEFAULT_PRICE, DEFAULT_STATUS);
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