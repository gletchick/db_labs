package org.gletchick.lab2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.model.Hall;
import org.gletchick.lab2.repository.HallRepositoryImpl;

import java.util.List;

public class HallController {

    @FXML
    private TableView<Hall> hallTable;
    @FXML
    private TableColumn<Hall, Integer> idCol;
    @FXML
    private TableColumn<Hall, String> nameCol;
    @FXML
    private TableColumn<Hall, Integer> capacityCol;

    private final HallRepositoryImpl hallRepository = new HallRepositoryImpl();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("idHall"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("hallName"));
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        loadData();
    }

    private void loadData() {
        List<Hall> allHalls = hallRepository.findAll();
        hallTable.setItems(FXCollections.observableArrayList(allHalls));
    }
}