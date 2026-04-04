package org.gletchick.db.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.gletchick.db.factory.ServiceFactory;
import org.gletchick.db.model.*;
import org.gletchick.db.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminPanelController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, String> colClientLogin;
    @FXML private TableColumn<Client, String> colClientName;
    @FXML private TableColumn<Client, String> colClientSurname;
    @FXML private TableColumn<Client, String> colClientPhone;

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private TextField nameField;
    @FXML private TextField surnameField;
    @FXML private TextField patronymicField;
    @FXML private TextField phoneField;
    @FXML private TableView<Hall> hallTable;
    @FXML private TableColumn<Hall, Integer> colHallId;
    @FXML private TableColumn<Hall, String> colHallName;
    @FXML private TableColumn<Hall, Integer> colHallCapacity;

    @FXML private TextField hallNameField;
    @FXML private TextField hallCapacityField;
    @FXML private TableView<Seat> seatTable;
    @FXML private TableColumn<Seat, Integer> colSeatId;
    @FXML private TableColumn<Seat, Hall> colSeatHall; // Тип Hall
    @FXML private TableColumn<Seat, Integer> colSeatRow;
    @FXML private TableColumn<Seat, Integer> colSeatNumber;

    @FXML private ComboBox<Hall> seatHallComboBox;
    @FXML private TextField seatRowField;
    @FXML private TextField seatNumberField;
    @FXML private TableView<Session> sessionTable;
    @FXML private TableColumn<Session, Integer> colSessionId;
    @FXML private TableColumn<Session, Spectacle> colSessionSpectacle;
    @FXML private TableColumn<Session, Hall> colSessionHall;
    @FXML private TableColumn<Session, LocalDateTime> colSessionDateTime;

    @FXML private ComboBox<Spectacle> sessionSpectacleComboBox;
    @FXML private ComboBox<Hall> sessionHallComboBox;
    @FXML private DatePicker sessionDatePicker;
    @FXML private TextField sessionTimeField;
    @FXML private TableView<Spectacle> spectacleTable;
    @FXML private TableColumn<Spectacle, Integer> colSpecId;
    @FXML private TableColumn<Spectacle, String> colSpecTitle;
    @FXML private TableColumn<Spectacle, String> colSpecGenre;
    @FXML private TableColumn<Spectacle, Integer> colSpecDuration;

    @FXML private TextField specTitleField;
    @FXML private TextField specGenreField;
    @FXML private TextField specDurationField;

    @FXML private TableView<Ticket> ticketTable;
    @FXML private TableColumn<Ticket, Integer> colTicketId;
    @FXML private TableColumn<Ticket, Session> colTicketSession;
    @FXML private TableColumn<Ticket, Client> colTicketClient;
    @FXML private TableColumn<Ticket, Seat> colTicketSeat;
    @FXML private TableColumn<Ticket, Double> colTicketPrice;
    @FXML private TableColumn<Ticket, TicketStatus> colTicketStatus;

    @FXML private ComboBox<Session> ticketSessionComboBox;
    @FXML private ComboBox<Client> ticketClientComboBox;
    @FXML private ComboBox<Seat> ticketSeatComboBox;
    @FXML private ComboBox<TicketStatus> ticketStatusComboBox;
    @FXML private TextField ticketPriceField;

    private final ObservableList<Ticket> ticketData = FXCollections.observableArrayList();
    private final TicketService ticketService = ServiceFactory.getInstance().getTicketService();

    private final ObservableList<Spectacle> spectacleData = FXCollections.observableArrayList();
    private final SpectacleService spectacleService = ServiceFactory.getInstance().getSpectacleService();
    private final ObservableList<Session> sessionData = FXCollections.observableArrayList();
    private final SessionService sessionService = ServiceFactory.getInstance().getSessionService();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final ObservableList<Seat> seatData = FXCollections.observableArrayList();
    private final SeatService seatService = ServiceFactory.getInstance().getSeatService();
    private final ObservableList<Hall> hallData = FXCollections.observableArrayList();
    private final HallService hallService = ServiceFactory.getInstance().getHallService();
    private final ObservableList<Client> clientData = FXCollections.observableArrayList();
    private final ClientService clientService = ServiceFactory.getInstance().getClientService();

    @FXML
    public void initialize() {
        colClientLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colClientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colClientSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colClientPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        loadClientData();

        clientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillFields(newSelection);
            }
        });

        initHallTab();
        initSeatTab();
        initSessionTab();
        initSpectacleTab();
        initTicketTab();
    }

    private void initTicketTab() {
        colTicketId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTicketSession.setCellValueFactory(new PropertyValueFactory<>("session"));
        colTicketClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colTicketSeat.setCellValueFactory(new PropertyValueFactory<>("seat"));
        colTicketPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colTicketStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colTicketSession.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Session s, boolean e) {
                super.updateItem(s, e);
                setText(e || s == null ? "" : s.toString()); // Используем ваш переопределенный toString()
            }
        });
        colTicketClient.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Client cl, boolean e) {
                super.updateItem(cl, e);
                setText(e || cl == null ? "" : cl.getSurname() + " " + cl.getName());
            }
        });
        colTicketSeat.setCellFactory(c -> new TableCell<>() {
            @Override protected void updateItem(Seat st, boolean e) {
                super.updateItem(st, e);
                setText(e || st == null ? "" : "Ряд " + st.getRowNumber() + ", Место " + st.getSeatNumber());
            }
        });

        loadTicketData();
        updateTicketComboBoxes();

        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) fillTicketFields(newV);
        });
    }

    private void loadTicketData() {
        ticketData.setAll(ticketService.findAll());
        ticketTable.setItems(ticketData);
    }

    private void updateTicketComboBoxes() {
        ticketSessionComboBox.setItems(FXCollections.observableArrayList(ServiceFactory.getInstance().getSessionService().findAll()));
        ticketClientComboBox.setItems(FXCollections.observableArrayList(ServiceFactory.getInstance().getClientService().findAll()));
        ticketSeatComboBox.setItems(FXCollections.observableArrayList(ServiceFactory.getInstance().getSeatService().findAll()));
        ticketStatusComboBox.setItems(FXCollections.observableArrayList(TicketStatus.values())); // Загрузка всех вариантов из Enum

        // Кастомные конвертеры для ComboBox аналогично предыдущим сущностям...
        // (Например, для сеанса используйте s.toString(), для клиента cl.getLogin() и т.д.)
    }

    private void fillTicketFields(Ticket ticket) {
        ticketSessionComboBox.setValue(ticket.getSession());
        ticketClientComboBox.setValue(ticket.getClient());
        ticketSeatComboBox.setValue(ticket.getSeat());
        ticketStatusComboBox.setValue(ticket.getStatus());
        ticketPriceField.setText(String.valueOf(ticket.getPrice()));
    }

    @FXML
    private void handleClearTicketFields() {
        ticketTable.getSelectionModel().clearSelection();
        ticketSessionComboBox.setValue(null);
        ticketClientComboBox.setValue(null);
        ticketSeatComboBox.setValue(null);
        ticketStatusComboBox.setValue(null);
        ticketPriceField.clear();
    }

    @FXML
    private void handleAddTicket() {
        try {
            Ticket t = new Ticket();
            updateTicketFromFields(t);
            ticketService.save(t);
            loadTicketData();
            handleClearTicketFields();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleUpdateTicket() {
        Ticket selected = ticketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateTicketFromFields(selected);
            ticketService.update(selected);
            ticketTable.refresh();
            handleClearTicketFields();
        }
    }

    @FXML
    private void handleDeleteTicket() {
        Ticket selected = ticketTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ticketService.deleteById(selected.getId());
            ticketData.remove(selected);
            handleClearTicketFields();
        }
    }

    private void updateTicketFromFields(Ticket t) {
        t.setSession(ticketSessionComboBox.getValue());
        t.setClient(ticketClientComboBox.getValue());
        t.setSeat(ticketSeatComboBox.getValue());
        t.setStatus(ticketStatusComboBox.getValue());
        t.setPrice(Double.parseDouble(ticketPriceField.getText()));
    }

    private void initSpectacleTab() {
        colSpecId.setCellValueFactory(new PropertyValueFactory<>("idSpectacle"));
        colSpecTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSpecGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));
        colSpecDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));

        loadSpectacleData();

        spectacleTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillSpecFields(newVal);
        });
    }

    private void loadSpectacleData() {
        spectacleData.setAll(spectacleService.findAll());
        spectacleTable.setItems(spectacleData);
    }

    private void fillSpecFields(Spectacle spec) {
        specTitleField.setText(spec.getTitle());
        specGenreField.setText(spec.getGenre());
        specDurationField.setText(String.valueOf(spec.getDuration()));
    }

    @FXML
    private void handleClearSpecFields() {
        spectacleTable.getSelectionModel().clearSelection();
        specTitleField.clear();
        specGenreField.clear();
        specDurationField.clear();
    }

    @FXML
    private void handleAddSpec() {
        try {
            Spectacle newSpec = new Spectacle();
            updateSpecFromFields(newSpec);
            spectacleService.save(newSpec);
            loadSpectacleData();
            handleClearSpecFields();
        } catch (NumberFormatException e) {
            // Ошибка формата числа в duration
        }
    }

    @FXML
    private void handleUpdateSpec() {
        Spectacle selected = spectacleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                updateSpecFromFields(selected);
                spectacleService.update(selected);
                spectacleTable.refresh();
                handleClearSpecFields();
            } catch (NumberFormatException e) {
                // Ошибка формата числа
            }
        }
    }

    @FXML
    private void handleDeleteSpec() {
        Spectacle selected = spectacleTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            spectacleService.deleteById(selected.getIdSpectacle());
            spectacleData.remove(selected);
            handleClearSpecFields();
        }
    }

    private void updateSpecFromFields(Spectacle spec) {
        spec.setTitle(specTitleField.getText());
        spec.setGenre(specGenreField.getText());
        spec.setDuration(Integer.parseInt(specDurationField.getText()));
    }

    private void initSessionTab() {
        colSessionId.setCellValueFactory(new PropertyValueFactory<>("idSession"));
        colSessionSpectacle.setCellValueFactory(new PropertyValueFactory<>("spectacle"));
        colSessionHall.setCellValueFactory(new PropertyValueFactory<>("hall"));
        colSessionDateTime.setCellValueFactory(new PropertyValueFactory<>("dateTimeStart"));

        // Кастомное отображение для Спектакля и Зала (используем названия вместо toString объектов)
        colSessionSpectacle.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Spectacle item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getTitle());
            }
        });

        colSessionHall.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Hall item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getHallName());
            }
        });

        loadSessionData();
        updateSessionComboBoxes();

        sessionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) fillSessionFields(newVal);
        });
    }

    private void loadSessionData() {
        sessionData.setAll(sessionService.findAll());
        sessionTable.setItems(sessionData);
    }

    private void updateSessionComboBoxes() {
        sessionSpectacleComboBox.setItems(FXCollections.observableArrayList(ServiceFactory.getInstance().getSpectacleService().findAll()));
        sessionHallComboBox.setItems(FXCollections.observableArrayList(ServiceFactory.getInstance().getHallService().findAll()));

        // Настройка отображения названий в ComboBox
        sessionSpectacleComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Spectacle s) { return s == null ? "" : s.getTitle(); }
            @Override public Spectacle fromString(String s) { return null; }
        });
        sessionHallComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Hall h) { return h == null ? "" : h.getHallName(); }
            @Override public Hall fromString(String s) { return null; }
        });
    }

    private void fillSessionFields(Session session) {
        sessionSpectacleComboBox.setValue(session.getSpectacle());
        sessionHallComboBox.setValue(session.getHall());
        if (session.getDateTimeStart() != null) {
            sessionDatePicker.setValue(session.getDateTimeStart().toLocalDate());
            sessionTimeField.setText(session.getDateTimeStart().toLocalTime().format(timeFormatter));
        }
    }

    @FXML
    private void handleClearSessionFields() {
        sessionTable.getSelectionModel().clearSelection();
        sessionSpectacleComboBox.setValue(null);
        sessionHallComboBox.setValue(null);
        sessionDatePicker.setValue(null);
        sessionTimeField.clear();
    }

    @FXML
    private void handleAddSession() {
        try {
            Session newSession = new Session();
            updateSessionFromFields(newSession);
            sessionService.save(newSession);
            loadSessionData();
            handleClearSessionFields();
        } catch (Exception e) {
            e.printStackTrace(); // Тут стоит добавить Alert об ошибке формата времени
        }
    }

    @FXML
    private void handleUpdateSession() {
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                updateSessionFromFields(selected);
                sessionService.update(selected);
                sessionTable.refresh();
                handleClearSessionFields();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteSession() {
        Session selected = sessionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            sessionService.deleteById(selected.getIdSession());
            sessionData.remove(selected);
            handleClearSessionFields();
        }
    }

    private void updateSessionFromFields(Session session) {
        session.setSpectacle(sessionSpectacleComboBox.getValue());
        session.setHall(sessionHallComboBox.getValue());

        LocalDate date = sessionDatePicker.getValue();
        LocalTime time = LocalTime.parse(sessionTimeField.getText(), timeFormatter);
        if (date != null && time != null) {
            session.setDateTimeStart(LocalDateTime.of(date, time));
        }
    }

    private void initSeatTab() {
        colSeatId.setCellValueFactory(new PropertyValueFactory<>("idSeat"));
        colSeatHall.setCellValueFactory(new PropertyValueFactory<>("hall"));
        colSeatRow.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        colSeatNumber.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));

        // Кастомное отображение зала в таблице (чтобы выводилось имя, а не адрес объекта)
        colSeatHall.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Hall item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getHallName());
            }
        });

        loadSeatData();
        updateHallComboBox(); // Заполняем список залов

        seatTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillSeatFields(newVal);
            }
        });
    }

    private void loadSeatData() {
        seatData.setAll(seatService.findAll());
        seatTable.setItems(seatData);
    }

    // Позволяет обновлять список залов в выпадающем меню (например, если добавили новый зал в соседней вкладке)
    private void updateHallComboBox() {
        List<Hall> halls = ServiceFactory.getInstance().getHallService().findAll();
        seatHallComboBox.setItems(FXCollections.observableArrayList(halls));

        // Как отображать Hall в самом ComboBox
        seatHallComboBox.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Hall hall) { return hall == null ? "" : hall.getHallName(); }
            @Override public Hall fromString(String string) { return null; }
        });
    }

    private void fillSeatFields(Seat seat) {
        seatHallComboBox.setValue(seat.getHall());
        seatRowField.setText(String.valueOf(seat.getRowNumber()));
        seatNumberField.setText(String.valueOf(seat.getSeatNumber()));
    }

    @FXML
    private void handleClearSeatFields() {
        seatTable.getSelectionModel().clearSelection();
        seatHallComboBox.setValue(null);
        seatRowField.clear();
        seatNumberField.clear();
    }

    @FXML
    private void handleAddSeat() {
        try {
            Seat newSeat = new Seat();
            newSeat.setHall(seatHallComboBox.getValue());
            newSeat.setRowNumber(Integer.parseInt(seatRowField.getText()));
            newSeat.setSeatNumber(Integer.parseInt(seatNumberField.getText()));

            seatService.save(newSeat);
            loadSeatData();
            handleClearSeatFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateSeat() {
        Seat selected = seatTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setHall(seatHallComboBox.getValue());
                selected.setRowNumber(Integer.parseInt(seatRowField.getText()));
                selected.setSeatNumber(Integer.parseInt(seatNumberField.getText()));

                seatService.update(selected);
                seatTable.refresh();
                handleClearSeatFields();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteSeat() {
        Seat selected = seatTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            seatService.deleteById(selected.getIdSeat());
            seatData.remove(selected);
            handleClearSeatFields();
        }
    }

    private void initHallTab() {
        colHallId.setCellValueFactory(new PropertyValueFactory<>("idHall"));
        colHallName.setCellValueFactory(new PropertyValueFactory<>("hallName"));
        colHallCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        loadHallData();

        hallTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillHallFields(newVal);
            }
        });
    }

    private void loadHallData() {
        hallData.setAll(hallService.findAll());
        hallTable.setItems(hallData);
    }

    private void fillHallFields(Hall hall) {
        hallNameField.setText(hall.getHallName());
        hallCapacityField.setText(String.valueOf(hall.getCapacity()));
    }

    private void loadClientData() {
        try {
            List<Client> clients = clientService.findAll();
            clientData.setAll(clients);
            clientTable.setItems(clientData);
        } catch (Exception e) {
            e.printStackTrace();
            // Здесь можно вызвать showAlert из BaseController, если он доступен
        }
    }

    private void fillFields(Client client) {
        loginField.setText(client.getLogin());
        passwordField.setText(client.getPassword()); // Будьте осторожны с отображением пароля
        nameField.setText(client.getName());
        surnameField.setText(client.getSurname());
        patronymicField.setText(client.getPatronymic());
        phoneField.setText(client.getPhone());
    }

    @FXML
    private void handleClearHallFields() {
        hallTable.getSelectionModel().clearSelection();
        hallNameField.clear();
        hallCapacityField.clear();
    }

    @FXML
    private void handleAddHall() {
        try {
            Hall newHall = new Hall();
            newHall.setHallName(hallNameField.getText());
            newHall.setCapacity(Integer.parseInt(hallCapacityField.getText()));

            hallService.save(newHall);
            loadHallData();
            handleClearHallFields();
        } catch (NumberFormatException e) {
            // Здесь можно вывести ошибку "Вместимость должна быть числом"
        }
    }

    @FXML
    private void handleUpdateHall() {
        Hall selected = hallTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setHallName(hallNameField.getText());
                selected.setCapacity(Integer.parseInt(hallCapacityField.getText()));

                hallService.update(selected);
                hallTable.refresh();
                handleClearHallFields();
            } catch (NumberFormatException e) {
            }
        }
    }

    @FXML
    private void handleDeleteHall() {
        Hall selected = hallTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            hallService.deleteById(selected.getIdHall());
            hallData.remove(selected);
            handleClearHallFields();
        }
    }

    @FXML
    private void handleClearFields() {
        clientTable.getSelectionModel().clearSelection();
        loginField.clear();
        passwordField.clear();
        nameField.clear();
        surnameField.clear();
        patronymicField.clear();
        phoneField.clear();
    }

    @FXML
    private void handleAddClient() {
        Client newClient = new Client();
        updateEntityFromFields(newClient);

        try {
            clientService.save(newClient);
            loadClientData(); // Перезагружаем список из БД для актуальности
            handleClearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateClient() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            updateEntityFromFields(selected);
            try {
                clientService.update(selected);
                clientTable.refresh();
                handleClearFields();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleDeleteClient() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                clientService.deleteById(selected.getId());
                clientData.remove(selected);
                handleClearFields();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateEntityFromFields(Client client) {
        client.setLogin(loginField.getText());
        client.setPassword(passwordField.getText());
        client.setName(nameField.getText());
        client.setSurname(surnameField.getText());
        client.setPatronymic(patronymicField.getText());
        client.setPhone(phoneField.getText());
    }
}