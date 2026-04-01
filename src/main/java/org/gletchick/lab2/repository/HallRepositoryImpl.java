package org.gletchick.lab2.repository;

import org.gletchick.lab2.model.Hall;
import org.gletchick.lab2.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HallRepositoryImpl implements Repository<Hall, Integer> {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM halls";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM halls WHERE id_hall = ?";

    @Override
    public List<Hall> findAll() {
        List<Hall> halls = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_QUERY)) {

            while (rs.next()) {
                halls.add(mapResultSetToHall(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return halls;
    }

    @Override
    public Hall findById(Integer id) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_QUERY)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToHall(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Hall mapResultSetToHall(ResultSet rs) throws SQLException {
        return new Hall(
                rs.getInt("id_hall"),
                rs.getString("hall_name"),
                rs.getInt("capacity")
        );
    }
}