package org.gletchick.lab2.db;

import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.Ticket;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DbManager {
    private final static String URL = "jdbc:postgresql://localhost:5432/TheaterDB";
    private final static String USER = "postgres";
    private final static String PASSWORD = "2006ukt,";

    private final static String INSERT_CLIENT = "INSERT INTO clients (phone, first_name, surname, patronymic) VALUES (?, ?, ?, ?)";
    private final static String SELECT_CLIENTS = "SELECT id_client AS id, phone, first_name, surname, patronymic FROM clients";
    private final static String UPDATE_CLIENT = "UPDATE clients SET phone=?, first_name=?, surname=?, patronymic=? WHERE id_client=?";
    private final static String DELETE_CLIENT = "DELETE FROM clients WHERE id_client=?";

    private final static String SELECT_TICKETS = "SELECT id_ticket AS id, id_session, id_seat, id_client, price, status FROM tickets";
    private final static String INSERT_TICKET = "INSERT INTO tickets (id_session, id_seat, id_client, price, status) VALUES (?, ?, ?, ?, ?)";
    private final static String UPDATE_TICKET = "UPDATE tickets SET price=?, status=? WHERE id_ticket=?";
    private final static String DELETE_TICKET = "DELETE FROM tickets WHERE id_ticket=?";

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

    public void syncAll(List<EntityState<Client>> clientStates, List<EntityState<Ticket>> ticketStates) throws SQLException {
        try (Connection conn = getNewConnection()) {
            conn.setAutoCommit(false);
            try {
                syncClientsInternal(conn, clientStates);
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
}