package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.gletchick.lab2.model.Session;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;
import org.gletchick.lab2.model.Spectacle;

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

    @FXML
    private ListView<Spectacle> listSpectacles; // fx:id="listSpectacles" в FXML

    @Override
    public void initialize() {
        super.initialize();
        loadSpectaclesToList();

        // Слушатель выбора спектакля
        listSpectacles.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadSessionsBySpectacle(newVal.getIdSpectacle());
            }
        });
    }

    @Override
    @FXML
    protected void saveChanges() {
        try {
            dbManager.syncAll(
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>(states)
            );
            // Перезагружаем текущий отфильтрованный вид
            Spectacle selected = listSpectacles.getSelectionModel().getSelectedItem();
            if (selected != null) loadSessionsBySpectacle(selected.getIdSpectacle());
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось сохранить", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected Session createEmptyEntity() {
        Spectacle selected = listSpectacles.getSelectionModel().getSelectedItem();
        int specId = (selected != null) ? selected.getIdSpectacle() : 1;
        return new Session(0, specId, 1, LocalDateTime.now().withSecond(0).withNano(0));
    }

    private void loadSpectaclesToList() {
        try {
            List<Spectacle> spectacles = dbManager.readTableSpectacles();
            listSpectacles.setItems(FXCollections.observableArrayList(spectacles));

            // Красивое отображение названия спектакля в списке
            listSpectacles.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(Spectacle item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTitle());
                    }
                }
            });
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить спектакли", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    private void loadSessionsBySpectacle(int specId) {
        try {
            // Используем новый метод из DbManager
            List<Session> sessions = dbManager.readSessionsBySpectacle(specId);
            states.clear();
            states.addAll(sessions.stream()
                    .map(s -> new EntityState<>(s, RowState.UNCHANGED))
                    .collect(Collectors.toList()));

            tableView.setItems(FXCollections.observableArrayList(sessions));
            updateFooter(sessions.size());
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка фильтрации сеансов", e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    @Override
    protected void loadData() {
        // При общей загрузке просто выбираем первый спектакль, что вызовет фильтрацию
        if (!listSpectacles.getItems().isEmpty()) {
            listSpectacles.getSelectionModel().selectFirst();
        }
    }

    @Override
    @FXML
    protected void cancelChanges() {
        Spectacle selected = listSpectacles.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadSessionsBySpectacle(selected.getIdSpectacle());
        } else {
            loadData();
        }
    }

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

    @FXML
    private void handleAdd() {
        addNewRow(createEmptyEntity());
    }

    @FXML
    private void handleDelete() {
        deleteSelected();
    }
}