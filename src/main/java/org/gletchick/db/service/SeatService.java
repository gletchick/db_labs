package org.gletchick.db.service;

import org.gletchick.db.model.Seat;

import java.util.List;

public interface SeatService extends Service<Seat, Integer> {
    List<Seat> findByHall(Integer hallId);
}