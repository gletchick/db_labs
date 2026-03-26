package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.gletchick.lab2.model.Ticket;
import org.gletchick.lab2.model.Session;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketController extends AbstractTableController<Ticket> {

    @FXML
    private ListView<Session> listSessions; // fx:id="listSessions"

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");

    @Override
    public void initialize() {
        super.initialize();
        loadSessionsToList();

        // Слушатель выбора сеанса
        listSessions.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadTicketsBySession(newVal.getIdSession());
            }
        });
    }

    private void loadSessionsToList() {
        try {
            List<Session> sessions = dbManager.readTableSessions();
            listSessions.setItems(FXCollections.observableArrayList(sessions));

            listSessions.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Session item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("Сеанс #" + item.getIdSession() + " [" + item.getDateTimeStart().format(timeFormatter) + "]");
                    }
                }
            });
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить сеансы", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    private void loadTicketsBySession(int sessionId) {
        try {
            List<Ticket> tickets = dbManager.readTicketsBySession(sessionId);
            states.clear();
            states.addAll(tickets.stream()
                    .map(t -> new EntityState<>(t, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(tickets));
            updateFooter(tickets.size());
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка фильтрации билетов", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected void setupColumns() {
        lblTitle.setText("Продажа билетов");
        tableView.setEditable(true);

        TableColumn<Ticket, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Ticket, Integer> colSeat = new TableColumn<>("ID Места");
        colSeat.setCellValueFactory(new PropertyValueFactory<>("idSeat"));
        colSeat.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colSeat.setOnEditCommit(e -> {
            e.getRowValue().setIdSeat(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        TableColumn<Ticket, Integer> colClient = new TableColumn<>("ID Клиента");
        colClient.setCellValueFactory(new PropertyValueFactory<>("idClient"));
        colClient.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colClient.setOnEditCommit(e -> {
            e.getRowValue().setIdClient(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        TableColumn<Ticket, Double> colPrice = new TableColumn<>("Цена");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colPrice.setOnEditCommit(e -> {
            e.getRowValue().setPrice(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        TableColumn<Ticket, String> colStatus = new TableColumn<>("Статус");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setCellFactory(TextFieldTableCell.forTableColumn());
        colStatus.setOnEditCommit(e -> {
            e.getRowValue().setStatus(e.getNewValue());
            markAsModified(e.getRowValue());
        });

        tableView.getColumns().addAll(colId, colSeat, colClient, colPrice, colStatus);
    }

    @Override
    protected void loadData() {
        if (!listSessions.getItems().isEmpty()) {
            listSessions.getSelectionModel().selectFirst();
        }
    }

    @Override
    @FXML
    protected void saveChanges() {
        try {
            dbManager.syncAll(new ArrayList<>(), new ArrayList<>(states), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            Session selected = listSessions.getSelectionModel().getSelectedItem();
            if (selected != null) loadTicketsBySession(selected.getIdSession());
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка сохранения", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    @FXML
    protected void cancelChanges() {
        Session selected = listSessions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadTicketsBySession(selected.getIdSession());
        } else {
            loadData();
        }
    }

    @Override
    protected Ticket createEmptyEntity() {
        Session selected = listSessions.getSelectionModel().getSelectedItem();
        int sessionId = (selected != null) ? selected.getIdSession() : 1;
        return new Ticket(0, sessionId, 1, 1, 500.0, "AVAILABLE");
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