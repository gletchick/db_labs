package org.gletchick.lab2.repository;

import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientRepositoryImpl implements Repository<Client, Integer> {

    private static final String SELECT_ALL_QUERY = "SELECT * FROM clients";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM clients WHERE id_client = ?";

    @Override
    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_QUERY)) {

            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    @Override
    public Client findById(Integer id) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_QUERY)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClient(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        return new Client(
                rs.getInt("id_client"),
                rs.getString("phone"),
                rs.getString("first_name"),
                rs.getString("surname"),
                rs.getString("patronymic")
        );
    }
}