package org.gletchick.db.repository.impl;

import jakarta.persistence.EntityManager;
import org.gletchick.db.model.Ticket;
import org.gletchick.db.model.TicketStatus;
import org.gletchick.db.repository.TicketRepository;
import org.gletchick.db.util.DbManager;

import java.util.List;

public class TicketRepositoryImpl extends BaseRepositoryImpl<Ticket, Integer> implements TicketRepository {

    public TicketRepositoryImpl() {
        super(Ticket.class);
    }

    public List<Ticket> findAvailableTicketsBySession(Integer sessionId) {
        EntityManager em = DbManager.getEntityManager();
        try {
            return em.createQuery(
                            "FROM Ticket t WHERE t.session.id = :sessionId AND t.status = :status", Ticket.class)
                    .setParameter("sessionId", sessionId)
                    .setParameter("status", TicketStatus.AVAILABLE) // Hibernate сам подставит строку "AVAILABLE"
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Integer> findOccupiedSeatIdsBySessionId(Integer sessionId) {
        EntityManager em = DbManager.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT t.seat.idSeat FROM Ticket t " +
                                    "WHERE t.session.idSession = :sessionId " +
                                    "AND t.status IN (:statuses)", Integer.class)
                    .setParameter("sessionId", sessionId)
                    .setParameter("statuses", List.of(TicketStatus.SOLD, TicketStatus.BOOKED))
                    .getResultList();
        } finally {
            em.close();
        }
    }
}