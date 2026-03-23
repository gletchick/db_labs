package org.gletchick.lab2.db;

import org.gletchick.lab2.model.*;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DbManager {
    private final static String URL = "jdbc:postgresql://localhost:5432/TheaterDB";
    private final static String USER = "postgres";
    private final static String PASSWORD = "2006ukt,";

    // Clients SQL
    private final static String INSERT_CLIENT = "INSERT INTO clients (phone, first_name, surname, patronymic) VALUES (?, ?, ?, ?)";
    private final static String SELECT_CLIENTS = "SELECT id_client AS id, phone, first_name, surname, patronymic FROM clients";
    private final static String UPDATE_CLIENT = "UPDATE clients SET phone=?, first_name=?, surname=?, patronymic=? WHERE id_client=?";
    private final static String DELETE_CLIENT = "DELETE FROM clients WHERE id_client=?";

    // Tickets SQL
    private final static String SELECT_TICKETS = "SELECT id_ticket AS id, id_session, id_seat, id_client, price, status FROM tickets";
    private final static String INSERT_TICKET = "INSERT INTO tickets (id_session, id_seat, id_client, price, status) VALUES (?, ?, ?, ?, ?)";
    private final static String UPDATE_TICKET = "UPDATE tickets SET price=?, status=? WHERE id_ticket=?";
    private final static String DELETE_TICKET = "DELETE FROM tickets WHERE id_ticket=?";

    // Spectacles SQL
    private final static String SELECT_SPECTACLES = "SELECT id_spectacle AS id, title, genre, duration FROM spectacles";
    private final static String INSERT_SPECTACLE = "INSERT INTO spectacles (title, genre, duration) VALUES (?, ?, ?)";
    private final static String UPDATE_SPECTACLE = "UPDATE spectacles SET title=?, genre=?, duration=? WHERE id_spectacle=?";
    private final static String DELETE_SPECTACLE = "DELETE FROM spectacles WHERE id_spectacle=?";

    // Halls SQL
    private final static String SELECT_HALLS = "SELECT id_hall AS id, hall_name, capacity FROM halls";
    private final static String INSERT_HALL = "INSERT INTO halls (hall_name, capacity) VALUES (?, ?)";
    private final static String UPDATE_HALL = "UPDATE halls SET hall_name=?, capacity=? WHERE id_hall=?";
    private final static String DELETE_HALL = "DELETE FROM halls WHERE id_hall=?";

    // Seats SQL
    private final static String SELECT_SEATS = "SELECT id_seat AS id, id_hall, row_number, seat_number FROM seats";
    private final static String INSERT_SEAT = "INSERT INTO seats (id_hall, row_number, seat_number) VALUES (?, ?, ?)";
    private final static String UPDATE_SEAT = "UPDATE seats SET id_hall=?, row_number=?, seat_number=? WHERE id_seat=?";
    private final static String DELETE_SEAT = "DELETE FROM seats WHERE id_seat=?";

    // Sessions SQL
    private final static String SELECT_SESSIONS = "SELECT id_session AS id, id_spectacle, id_hall, date_time_start FROM sessions";
    private final static String INSERT_SESSION = "INSERT INTO sessions (id_spectacle, id_hall, date_time_start) VALUES (?, ?, ?)";
    private final static String UPDATE_SESSION = "UPDATE sessions SET id_spectacle=?, id_hall=?, date_time_start=? WHERE id_session=?";
    private final static String DELETE_SESSION = "DELETE FROM sessions WHERE id_session=?";

    private Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public List<Client> readTableClients() throws SQLException {
        List<Client> clients = new LinkedList<>();
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_CLIENTS)) {
            while (rs.next()) {
                clients.add(new Client(
                        rs.getInt("id"),
                        rs.getString("phone"),
                        rs.getString("first_name"),
                        rs.getString("surname"),
                        rs.getString("patronymic")));
            }
        }
        return clients;
    }

    public List<Ticket> readTableTickets() throws SQLException {
        List<Ticket> tickets = new LinkedList<>();
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_TICKETS)) {
            while (rs.next()) {
                tickets.add(new Ticket(
                        rs.getInt("id"),
                        rs.getInt("id_session"),
                        rs.getInt("id_seat"),
                        rs.getInt("id_client"),
                        rs.getDouble("price"),
                        rs.getString("status")));
            }
        }
        return tickets;
    }

    public List<Spectacle> readTableSpectacles() throws SQLException {
        List<Spectacle> list = new LinkedList<>();
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_SPECTACLES)) {
            while (rs.next()) {
                list.add(new Spectacle(rs.getInt("id"), rs.getString("title"),
                        rs.getString("genre"), rs.getInt("duration")));
            }
        }
        return list;
    }

    public List<Hall> readTableHalls() throws SQLException {
        List<Hall> list = new LinkedList<>();
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_HALLS)) {
            while (rs.next()) {
                list.add(new Hall(rs.getInt("id"), rs.getString("hall_name"), rs.getInt("capacity")));
            }
        }
        return list;
    }

    public List<Seat> readTableSeats() throws SQLException {
        List<Seat> list = new LinkedList<>();
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_SEATS)) {
            while (rs.next()) {
                list.add(new Seat(rs.getInt("id"), rs.getInt("id_hall"),
                        rs.getInt("row_number"), rs.getInt("seat_number")));
            }
        }
        return list;
    }

    public List<Session> readTableSessions() throws SQLException {
        List<Session> list = new LinkedList<>();
        try (Connection conn = getNewConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_SESSIONS)) {
            while (rs.next()) {
                list.add(new Session(rs.getInt("id"), rs.getInt("id_spectacle"),
                        rs.getInt("id_hall"), rs.getTimestamp("date_time_start").toLocalDateTime()));
            }
        }
        return list;
    }

    public void syncAll(List<EntityState<Client>> clientStates,
                        List<EntityState<Ticket>> ticketStates,
                        List<EntityState<Spectacle>> spectacleStates,
                        List<EntityState<Hall>> hallStates,
                        List<EntityState<Seat>> seatStates,
                        List<EntityState<Session>> sessionStates) throws SQLException {
        try (Connection conn = getNewConnection()) {
            conn.setAutoCommit(false);
            try {
                syncClientsInternal(conn, clientStates);
                syncSpectaclesInternal(conn, spectacleStates);
                syncHallsInternal(conn, hallStates);
                syncSeatsInternal(conn, seatStates);
                syncSessionsInternal(conn, sessionStates);
                syncTicketsInternal(conn, ticketStates);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    private void syncClientsInternal(Connection conn, List<EntityState<Client>> states) throws SQLException {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_CLIENT);
             PreparedStatement upd = conn.prepareStatement(UPDATE_CLIENT);
             PreparedStatement del = conn.prepareStatement(DELETE_CLIENT)) {

            for (EntityState<Client> state : states) {
                Client c = state.getEntity();
                switch (state.getState()) {
                    case ADDED -> {
                        ins.setString(1, c.getPhone());
                        ins.setString(2, c.getName());
                        ins.setString(3, c.getSurname());
                        ins.setString(4, c.getPatronymic());
                        ins.addBatch();
                    }
                    case MODIFIED -> {
                        upd.setString(1, c.getPhone());
                        upd.setString(2, c.getName());
                        upd.setString(3, c.getSurname());
                        upd.setString(4, c.getPatronymic());
                        upd.setInt(5, c.getId());
                        upd.addBatch();
                    }
                    case DELETED -> {
                        del.setInt(1, c.getId());
                        del.addBatch();
                    }
                    case UNCHANGED -> {}
                }
            }
            ins.executeBatch();
            upd.executeBatch();
            del.executeBatch();
        }
    }

    private void syncTicketsInternal(Connection conn, List<EntityState<Ticket>> states) throws SQLException {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_TICKET);
             PreparedStatement upd = conn.prepareStatement(UPDATE_TICKET);
             PreparedStatement del = conn.prepareStatement(DELETE_TICKET)) {

            for (EntityState<Ticket> state : states) {
                Ticket t = state.getEntity();
                switch (state.getState()) {
                    case ADDED -> {
                        ins.setInt(1, t.getIdSession());
                        ins.setInt(2, t.getIdSeat());
                        ins.setInt(3, t.getIdClient());
                        ins.setDouble(4, t.getPrice());
                        ins.setString(5, t.getStatus());
                        ins.addBatch();
                    }
                    case MODIFIED -> {
                        upd.setDouble(1, t.getPrice());
                        upd.setString(2, t.getStatus());
                        upd.setInt(3, t.getId());
                        upd.addBatch();
                    }
                    case DELETED -> {
                        del.setInt(1, t.getId());
                        del.addBatch();
                    }
                    case UNCHANGED -> {}
                }
            }
            ins.executeBatch();
            upd.executeBatch();
            del.executeBatch();
        }
    }

    private void syncSpectaclesInternal(Connection conn, List<EntityState<Spectacle>> states) throws SQLException {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_SPECTACLE);
             PreparedStatement upd = conn.prepareStatement(UPDATE_SPECTACLE);
             PreparedStatement del = conn.prepareStatement(DELETE_SPECTACLE)) {

            for (EntityState<Spectacle> state : states) {
                Spectacle s = state.getEntity();
                switch (state.getState()) {
                    case ADDED -> {
                        ins.setString(1, s.getTitle());
                        ins.setString(2, s.getGenre());
                        ins.setInt(3, s.getDuration());
                        ins.addBatch();
                    }
                    case MODIFIED -> {
                        upd.setString(1, s.getTitle());
                        upd.setString(2, s.getGenre());
                        upd.setInt(3, s.getDuration());
                        upd.setInt(4, s.getIdSpectacle());
                        upd.addBatch();
                    }
                    case DELETED -> {
                        del.setInt(1, s.getIdSpectacle());
                        del.addBatch();
                    }
                    case UNCHANGED -> {}
                }
            }
            ins.executeBatch();
            upd.executeBatch();
            del.executeBatch();
        }
    }

    private void syncHallsInternal(Connection conn, List<EntityState<Hall>> states) throws SQLException {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_HALL);
             PreparedStatement upd = conn.prepareStatement(UPDATE_HALL);
             PreparedStatement del = conn.prepareStatement(DELETE_HALL)) {

            for (EntityState<Hall> state : states) {
                Hall h = state.getEntity();
                switch (state.getState()) {
                    case ADDED -> {
                        ins.setString(1, h.getHallName());
                        ins.setInt(2, h.getCapacity());
                        ins.addBatch();
                    }
                    case MODIFIED -> {
                        upd.setString(1, h.getHallName());
                        upd.setInt(2, h.getCapacity());
                        upd.setInt(3, h.getIdHall());
                        upd.addBatch();
                    }
                    case DELETED -> {
                        del.setInt(1, h.getIdHall());
                        del.addBatch();
                    }
                    case UNCHANGED -> {}
                }
            }
            ins.executeBatch();
            upd.executeBatch();
            del.executeBatch();
        }
    }

    private void syncSeatsInternal(Connection conn, List<EntityState<Seat>> states) throws SQLException {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_SEAT);
             PreparedStatement upd = conn.prepareStatement(UPDATE_SEAT);
             PreparedStatement del = conn.prepareStatement(DELETE_SEAT)) {

            for (EntityState<Seat> state : states) {
                Seat s = state.getEntity();
                switch (state.getState()) {
                    case ADDED -> {
                        ins.setInt(1, s.getIdHall());
                        ins.setInt(2, s.getRowNumber());
                        ins.setInt(3, s.getSeatNumber());
                        ins.addBatch();
                    }
                    case MODIFIED -> {
                        upd.setInt(1, s.getIdHall());
                        upd.setInt(2, s.getRowNumber());
                        upd.setInt(3, s.getSeatNumber());
                        upd.setInt(4, s.getIdSeat());
                        upd.addBatch();
                    }
                    case DELETED -> {
                        del.setInt(1, s.getIdSeat());
                        del.addBatch();
                    }
                    case UNCHANGED -> {}
                }
            }
            ins.executeBatch();
            upd.executeBatch();
            del.executeBatch();
        }
    }

    private void syncSessionsInternal(Connection conn, List<EntityState<Session>> states) throws SQLException {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_SESSION);
             PreparedStatement upd = conn.prepareStatement(UPDATE_SESSION);
             PreparedStatement del = conn.prepareStatement(DELETE_SESSION)) {

            for (EntityState<Session> state : states) {
                Session s = state.getEntity();
                switch (state.getState()) {
                    case ADDED -> {
                        ins.setInt(1, s.getIdSpectacle());
                        ins.setInt(2, s.getIdHall());
                        ins.setTimestamp(3, Timestamp.valueOf(s.getDateTimeStart()));
                        ins.addBatch();
                    }
                    case MODIFIED -> {
                        upd.setInt(1, s.getIdSpectacle());
                        upd.setInt(2, s.getIdHall());
                        upd.setTimestamp(3, Timestamp.valueOf(s.getDateTimeStart()));
                        upd.setInt(4, s.getIdSession());
                        upd.addBatch();
                    }
                    case DELETED -> {
                        del.setInt(1, s.getIdSession());
                        del.addBatch();
                    }
                    case UNCHANGED -> {}
                }
            }
            ins.executeBatch();
            upd.executeBatch();
            del.executeBatch();
        }
    }

}