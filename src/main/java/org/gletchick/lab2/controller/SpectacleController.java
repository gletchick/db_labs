package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.model.Spectacle;
import org.gletchick.lab2.repository.SpectacleRepositoryImpl;

import java.util.List;

public class SpectacleController {

    @FXML
    private TableView<Spectacle> spectacleTable;
    @FXML
    private TableColumn<Spectacle, Integer> idCol;
    @FXML
    private TableColumn<Spectacle, String> titleCol;
    @FXML
    private TableColumn<Spectacle, String> genreCol;
    @FXML
    private TableColumn<Spectacle, Integer> durationCol;

    private final SpectacleRepositoryImpl spectacleRepository = new SpectacleRepositoryImpl();

    @FXML
    public void initialize() {
        // Связываем колонки с полями модели Spectacle
        idCol.setCellValueFactory(new PropertyValueFactory<>("idSpectacle"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));

        loadData();
    }

    private void loadData() {
        List<Spectacle> allSpectacles = spectacleRepository.findAll();
        spectacleTable.setItems(FXCollections.observableArrayList(allSpectacles));
    }
}