package org.gletchick.lab2.repository;

import org.gletchick.lab2.model.*;
import org.gletchick.lab2.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketRepositoryImpl implements Repository<Ticket, Integer> {

    private static final String SELECT_ALL_WITH_RELATIONS =
            "SELECT t.*, " +
                    "s.date_time_start, s.id_spectacle, s.id_hall, " +
                    "spec.title, spec.genre, spec.duration, " +
                    "h.hall_name, h.capacity, " +
                    "c.phone, c.first_name, c.surname, c.patronymic " +
                    "FROM tickets t " +
                    "JOIN sessions s ON t.id_session = s.id_session " +
                    "JOIN spectacles spec ON s.id_spectacle = spec.id_spectacle " +
                    "JOIN halls h ON s.id_hall = h.id_hall " +
                    "JOIN clients c ON t.id_client = c.id_client";

    @Override
    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_WITH_RELATIONS)) {

            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    @Override
    public Ticket findById(Integer id) {
        String query = SELECT_ALL_WITH_RELATIONS + " WHERE t.id_ticket = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        Client client = new Client(
                rs.getInt("id_client"),
                rs.getString("phone"),
                rs.getString("first_name"),
                rs.getString("surname"),
                rs.getString("patronymic")
        );

        Spectacle spectacle = new Spectacle(
                rs.getInt("id_spectacle"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("duration")
        );

        Hall hall = new Hall(
                rs.getInt("id_hall"),
                rs.getString("hall_name"),
                rs.getInt("capacity")
        );

        Session session = new Session(
                rs.getInt("id_session"),
                spectacle,
                hall,
                rs.getTimestamp("date_time_start").toLocalDateTime()
        );

        return new Ticket(
                rs.getInt("id_ticket"),
                session,
                client,
                rs.getDouble("price"),
                rs.getString("status")
        );
    }
}