package org.gletchick.lab2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.model.Seat;
import org.gletchick.lab2.repository.SeatRepositoryImpl;

import java.util.List;

public class SeatController {

    @FXML
    private TableView<Seat> seatTable;
    @FXML
    private TableColumn<Seat, Integer> idCol;
    @FXML
    private TableColumn<Seat, String> hallCol;
    @FXML
    private TableColumn<Seat, Integer> rowCol;
    @FXML
    private TableColumn<Seat, Integer> seatNumberCol;

    private final SeatRepositoryImpl seatRepository = new SeatRepositoryImpl();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("idSeat"));
        rowCol.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        seatNumberCol.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));

        // Извлекаем название зала из вложенного объекта Hall
        hallCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHall().getHallName())
        );

        loadData();
    }

    private void loadData() {
        List<Seat> allSeats = seatRepository.findAll();
        seatTable.setItems(FXCollections.observableArrayList(allSeats));
    }
}