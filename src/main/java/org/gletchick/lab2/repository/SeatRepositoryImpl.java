package org.gletchick.lab2.repository;

import org.gletchick.lab2.model.Hall;
import org.gletchick.lab2.model.Seat;
import org.gletchick.lab2.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeatRepositoryImpl implements Repository<Seat, Integer> {

    private static final String SELECT_ALL_WITH_HALL =
            "SELECT s.*, h.hall_name, h.capacity FROM seats s " +
                    "JOIN halls h ON s.id_hall = h.id_hall";

    @Override
    public List<Seat> findAll() {
        List<Seat> seats = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_WITH_HALL)) {

            while (rs.next()) {
                seats.add(mapResultSetToSeat(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }

    @Override
    public Seat findById(Integer id) {
        String query = SELECT_ALL_WITH_HALL + " WHERE s.id_seat = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeat(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Seat mapResultSetToSeat(ResultSet rs) throws SQLException {
        Hall hall = new Hall(
                rs.getInt("id_hall"),
                rs.getString("hall_name"),
                rs.getInt("capacity")
        );

        return new Seat(
                rs.getInt("id_seat"),
                hall,
                rs.getInt("row_number"),
                rs.getInt("seat_number")
        );
    }
}