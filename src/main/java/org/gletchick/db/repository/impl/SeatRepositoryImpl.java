package org.gletchick.db.repository.impl;

import jakarta.persistence.EntityManager;
import org.gletchick.db.model.Seat;
import org.gletchick.db.repository.SeatRepository;
import org.gletchick.db.util.DbManager;

import java.util.List;

public class SeatRepositoryImpl extends BaseRepositoryImpl<Seat, Integer> implements SeatRepository {
    public SeatRepositoryImpl() {
        super(Seat.class);
    }

    @Override
    public List<Seat> findByHallId(Integer hallId) {
        EntityManager em = DbManager.getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Seat s WHERE s.hall.idHall = :id", Seat.class)
                    .setParameter("id", hallId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}