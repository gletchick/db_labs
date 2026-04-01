package org.gletchick.lab2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.lab2.model.Session;
import org.gletchick.lab2.repository.SessionRepositoryImpl;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SessionController {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";

    @FXML
    private TableView<Session> sessionTable;
    @FXML
    private TableColumn<Session, Integer> idCol;
    @FXML
    private TableColumn<Session, String> spectacleCol;
    @FXML
    private TableColumn<Session, String> hallCol;
    @FXML
    private TableColumn<Session, String> dateTimeCol;

    private final SessionRepositoryImpl sessionRepository = new SessionRepositoryImpl();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("idSession"));

        // Извлекаем название спектакля
        spectacleCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getSpectacle().getTitle())
        );

        // Извлекаем название зала
        hallCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getHall().getHallName())
        );

        // Форматируем дату и время
        dateTimeCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDateTimeStart().format(formatter))
        );

        loadData();
    }

    private void loadData() {
        List<Session> allSessions = sessionRepository.findAll();
        sessionTable.setItems(FXCollections.observableArrayList(allSessions));
    }
}