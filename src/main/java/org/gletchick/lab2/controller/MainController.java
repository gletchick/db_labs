package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    @FXML
    private ListView<String> menuList;
    @FXML
    private StackPane contentArea;

    private final Map<String, String> viewRoutes = new HashMap<>();
    private TableView<?> currentTableView; // Храним ссылку на активную таблицу

    @FXML
    public void initialize() {
        viewRoutes.put("Клиенты", "/org/gletchick/lab2/ui/ClientView.fxml");
        viewRoutes.put("Спектакли", "/org/gletchick/lab2/ui/SpectacleView.fxml");
        viewRoutes.put("Залы", "/org/gletchick/lab2/ui/HallView.fxml");
        viewRoutes.put("Места", "/org/gletchick/lab2/ui/SeatView.fxml");
        viewRoutes.put("Сеансы", "/org/gletchick/lab2/ui/SessionView.fxml");
        viewRoutes.put("Билеты", "/org/gletchick/lab2/ui/TicketView.fxml");

        menuList.setItems(FXCollections.observableArrayList(viewRoutes.keySet()));
        menuList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadTable(viewRoutes.get(newVal));
        });
    }

    private void loadTable(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            contentArea.getChildren().setAll(view);

            // Поиск TableView в загруженном FXML
            findTableView(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Рекурсивный поиск TableView в контейнере
    private void findTableView(Node node) {
        if (node instanceof TableView) {
            currentTableView = (TableView<?>) node;
        } else if (node instanceof javafx.scene.layout.Pane) {
            for (Node child : ((javafx.scene.layout.Pane) node).getChildren()) {
                findTableView(child);
            }
        }
    }

    @FXML
    private void handleExportAction() {
        if (currentTableView == null || currentTableView.getItems().isEmpty()) {
            System.out.println("Нет данных для экспорта или таблица не выбрана");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(contentArea.getScene().getWindow());

        if (file != null) {
            saveTableToCSV(file);
        }
    }

    private void saveTableToCSV(File file) {
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            // Получаем список колонок
            ObservableList<? extends TableColumn<?, ?>> columns = currentTableView.getColumns();

            // 1. Записываем заголовки
            for (int i = 0; i < columns.size(); i++) {
                String header = columns.get(i).getText();
                writer.print(header + (i == columns.size() - 1 ? "" : ","));
            }
            writer.println();

            // 2. Записываем данные строк
            for (Object item : currentTableView.getItems()) {
                for (int i = 0; i < columns.size(); i++) {
                    // Используем сырой тип TableColumn для обхода ошибки компиляции
                    TableColumn rawColumn = columns.get(i);
                    Object value = rawColumn.getCellData(item);

                    String cellValue = (value != null) ? value.toString().replace(",", ";") : "";
                    writer.print(cellValue + (i == columns.size() - 1 ? "" : ","));
                }
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла: " + e.getMessage());
        }
    }
}
