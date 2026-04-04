package org.gletchick.db.repository;

import org.gletchick.db.model.Seat;

import java.util.List;

public interface SeatRepository extends CrudRepository<Seat, Integer> {
    List<Seat> findByHallId(Integer hallId);
}