package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;

import java.util.Optional;

public abstract class AbstractTableController<T> {

    @FXML protected Button btnFirst, btnPrev, btnNext, btnLast, btnAdd, btnDelete, btnSave;
    @FXML protected TextField txtCurrentIndex;
    @FXML protected Label lblTotalCount, lblTitle;
    @FXML protected TableView<T> tableView;

    protected final ObservableList<EntityState<T>> states = FXCollections.observableArrayList();
    protected final DbManager dbManager = new DbManager();

    @FXML
    public void initialize() {
        setupColumns();
        loadData();
        setupNavigationLogic();
        setupActionHandlers();
    }

    /**
     * Конфигурация колонок TableView и привязка к полям модели.
     */
    protected abstract void setupColumns();

    /**
     * Загрузка данных из БД через DbManager.
     */
    protected abstract void loadData();

    /**
     * Вызов dbManager.syncAll с актуальными списками состояний.
     */
    @FXML
    protected abstract void saveChanges();

    /**
     * Создание нового экземпляра сущности с дефолтными значениями.
     */
    protected abstract T createEmptyEntity();

    private void setupNavigationLogic() {
        tableView.getSelectionModel().selectedIndexProperty().addListener((obs, oldIdx, newIdx) -> {
            updateNavigationUI(newIdx.intValue());
        });

        btnFirst.setOnAction(e -> tableView.getSelectionModel().selectFirst());
        btnLast.setOnAction(e -> tableView.getSelectionModel().selectLast());
        btnNext.setOnAction(e -> tableView.getSelectionModel().selectNext());
        btnPrev.setOnAction(e -> tableView.getSelectionModel().selectPrevious());

        // Позволяет переместиться к записи, введя номер в TextField
        txtCurrentIndex.setOnAction(e -> {
            try {
                int targetIdx = Integer.parseInt(txtCurrentIndex.getText()) - 1;
                tableView.getSelectionModel().select(targetIdx);
            } catch (NumberFormatException ignored) {}
        });
    }

    private void setupActionHandlers() {
        btnAdd.setOnAction(e -> handleAddAction());
        btnDelete.setOnAction(e -> handleDeleteAction());
        btnSave.setOnAction(e -> saveChanges());
    }

    private void updateNavigationUI(int currentIndex) {
        int displayIdx = currentIndex + 1;
        txtCurrentIndex.setText(String.valueOf(displayIdx > 0 ? displayIdx : 0));
    }

    protected void updateFooter(int count) {
        lblTotalCount.setText("для " + count);
    }

    protected void handleAddAction() {
        T entity = createEmptyEntity();
        states.add(new EntityState<>(entity, RowState.ADDED));
        tableView.getItems().add(entity);
        tableView.getSelectionModel().select(entity);
        updateFooter(tableView.getItems().size());
    }

    protected void handleDeleteAction() {
        T selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Optional<EntityState<T>> stateOpt = states.stream()
                .filter(s -> s.getEntity().equals(selected))
                .findFirst();

        stateOpt.ifPresent(state -> {
            if (state.getState() == RowState.ADDED) {
                states.remove(state);
            } else {
                state.setState(RowState.DELETED);
            }
            tableView.getItems().remove(selected);
            updateFooter(tableView.getItems().size());
        });
    }

    /**
     * Меняет статус сущности на MODIFIED, если текущий статус UNCHANGED.
     * Вызывается в OnEditCommit колонок таблицы.
     */
    protected void markAsModified(T entity) {
        states.stream()
                .filter(s -> s.getEntity().equals(entity))
                .findFirst()
                .ifPresent(s -> {
                    if (s.getState() == RowState.UNCHANGED) {
                        s.setState(RowState.MODIFIED);
                    }
                });
    }

    protected void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Добавляет новую пустую сущность в таблицу и помечает её как ADDED
     */
    protected void addNewRow(T entity) {
        states.add(new EntityState<>(entity, RowState.ADDED));
        tableView.getItems().add(entity);
        tableView.getSelectionModel().select(entity);
        updateFooter(tableView.getItems().size());
    }

    /**
     * Удаляет выбранную строку:
     * - Если она была только что добавлена (ADDED), просто удаляет из списков.
     * - Если она уже была в БД, помечает как DELETED для синхронизации.
     */
    protected void deleteSelected() {
        T selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        states.stream()
                .filter(s -> s.getEntity().equals(selected))
                .findFirst()
                .ifPresent(state -> {
                    if (state.getState() == RowState.ADDED) {
                        states.remove(state);
                    } else {
                        state.setState(RowState.DELETED);
                    }
                    tableView.getItems().remove(selected);
                    updateFooter(tableView.getItems().size());
                });
    }
}