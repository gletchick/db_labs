package org.gletchick.db.repository.impl;

import org.gletchick.db.model.Seat;
import org.gletchick.db.repository.BaseRepositoryImpl;
import org.gletchick.db.repository.SeatRepository;

public class SeatRepositoryImpl extends BaseRepositoryImpl<Seat, Integer> implements SeatRepository {
    public SeatRepositoryImpl() {
        super(Seat.class);
    }
}