package org.gletchick.db.service.impl;

import lombok.RequiredArgsConstructor;
import org.gletchick.db.model.Seat;
import org.gletchick.db.repository.CrudRepository;
import org.gletchick.db.repository.SeatRepository;
import org.gletchick.db.service.SeatService;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class SeatServiceImpl extends BaseService<Seat, Integer> implements SeatService {
    private final SeatRepository repository;

    @Override
    protected CrudRepository<Seat, Integer> getRepository() {
        return repository;
    }

    @Override
    public List<Seat> findByHall(Integer hallId) {
        if (hallId == null) return Collections.emptyList();
        return ((SeatRepository) getRepository()).findByHallId(hallId);
    }
}