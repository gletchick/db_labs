package org.gletchick.lab2.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.repository.ClientRepository;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ClientController {
    @FXML private TableView<EntityState<Client>> clientTable1;
    @FXML private TableColumn<EntityState<Client>, String> colComputed, col1Surname, col1Name, col1Phone;

    @FXML private TableView<EntityState<Client>> clientTable2;
    @FXML private TableColumn<EntityState<Client>, String> col2Surname, col2Name, col2Phone;

    @FXML private ComboBox<String> comboFilterSurname;
    @FXML private ComboBox<String> comboFilterPhone;

    private final ClientRepository repository = new ClientRepository();
    private FilteredList<EntityState<Client>> filteredData;

    @FXML
    public void initialize() {
        setupColumns();

        filteredData = new FilteredList<>(repository.getClientStates(), p -> true);

        comboFilterSurname.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        comboFilterPhone.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());

        SortedList<EntityState<Client>> sortedDataByName = new SortedList<>(filteredData);
        sortedDataByName.setComparator(Comparator.comparing(state -> state.getEntity().getName()));
        clientTable1.setItems(sortedDataByName);

        SortedList<EntityState<Client>> sortedDataByPhone = new SortedList<>(filteredData);
        sortedDataByPhone.setComparator(Comparator.comparing(state -> state.getEntity().getPhone()));
        clientTable2.setItems(sortedDataByPhone);
    }

    private void setupColumns() {
        colComputed.setCellValueFactory(data -> {
            Client c = data.getValue().getEntity();
            String initials = "";
            if (c.getName() != null && !c.getName().isEmpty()) initials += c.getName().charAt(0) + ".";
            if (c.getPatronymic() != null && !c.getPatronymic().isEmpty()) initials += c.getPatronymic().charAt(0) + ".";
            return new SimpleStringProperty(c.getSurname() + " " + initials);
        });

        col1Surname.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getSurname()));
        col1Name.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getName()));
        col1Phone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getPhone()));

        col2Surname.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getSurname()));
        col2Name.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getName()));
        col2Phone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEntity().getPhone()));
    }

    private void updateFilter() {
        String filterSurname = comboFilterSurname.getValue();
        String filterPhone = comboFilterPhone.getValue();

        filteredData.setPredicate(state -> {
            Client c = state.getEntity();

            boolean matchesSurname = (filterSurname == null || filterSurname.equals("Все") || c.getSurname().equals(filterSurname));
            boolean matchesPhone = (filterPhone == null || filterPhone.equals("Все") || c.getPhone().equals(filterPhone));

            return matchesSurname && matchesPhone;
        });
    }

    @FXML
    private void handleClearFilters() {
        comboFilterSurname.setValue("Все");
        comboFilterPhone.setValue("Все");
    }

    @FXML
    private void handleRefresh() {
        try {
            repository.loadFromDb();
            populateFilterComboBoxes();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Ошибка загрузки: " + e.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void populateFilterComboBoxes() {
        ObservableList<String> surnames = FXCollections.observableArrayList("Все");
        surnames.addAll(repository.getClientStates().stream()
                .map(state -> state.getEntity().getSurname())
                .distinct()
                .collect(Collectors.toList()));
        comboFilterSurname.setItems(surnames);

        ObservableList<String> phones = FXCollections.observableArrayList("Все");
        phones.addAll(repository.getClientStates().stream()
                .map(state -> state.getEntity().getPhone())
                .distinct()
                .collect(Collectors.toList()));
        comboFilterPhone.setItems(phones);
    }
}