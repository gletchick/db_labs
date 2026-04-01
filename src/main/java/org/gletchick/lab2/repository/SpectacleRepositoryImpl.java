package org.gletchick.lab2.repository;

import org.gletchick.lab2.model.Spectacle;
import org.gletchick.lab2.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpectacleRepositoryImpl implements Repository<Spectacle, Integer> {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM spectacles";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM spectacles WHERE id_spectacle = ?";

    @Override
    public List<Spectacle> findAll() {
        List<Spectacle> spectacles = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_QUERY)) {

            while (rs.next()) {
                spectacles.add(mapResultSetToSpectacle(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return spectacles;
    }

    @Override
    public Spectacle findById(Integer id) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_QUERY)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSpectacle(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Spectacle mapResultSetToSpectacle(ResultSet rs) throws SQLException {
        return new Spectacle(
                rs.getInt("id_spectacle"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("duration")
        );
    }
}