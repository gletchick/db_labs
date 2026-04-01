package org.gletchick.lab2.repository;

import org.gletchick.lab2.model.Hall;
import org.gletchick.lab2.model.Session;
import org.gletchick.lab2.model.Spectacle;
import org.gletchick.lab2.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionRepositoryImpl implements Repository<Session, Integer> {

    private static final String SELECT_ALL_WITH_RELATIONS =
            "SELECT s.*, " +
                    "spec.title AS spectacle_title, spec.genre, spec.duration, " +
                    "h.hall_name, h.capacity " +
                    "FROM sessions s " +
                    "JOIN spectacles spec ON s.id_spectacle = spec.id_spectacle " +
                    "JOIN halls h ON s.id_hall = h.id_hall";

    @Override
    public List<Session> findAll() {
        List<Session> sessions = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_WITH_RELATIONS)) {

            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    @Override
    public Session findById(Integer id) {
        String query = SELECT_ALL_WITH_RELATIONS + " WHERE s.id_session = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSession(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Session mapResultSetToSession(ResultSet rs) throws SQLException {
        Spectacle spectacle = new Spectacle(
                rs.getInt("id_spectacle"),
                rs.getString("spectacle_title"),
                rs.getString("genre"),
                rs.getInt("duration")
        );

        Hall hall = new Hall(
                rs.getInt("id_hall"),
                rs.getString("hall_name"),
                rs.getInt("capacity")
        );

        return new Session(
                rs.getInt("id_session"),
                spectacle,
                hall,
                rs.getTimestamp("date_time_start").toLocalDateTime()
        );
    }
}