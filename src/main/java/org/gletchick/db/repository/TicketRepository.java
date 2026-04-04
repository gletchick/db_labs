package org.gletchick.db.repository;

import org.gletchick.db.model.Ticket;

import java.util.List;

public interface TicketRepository extends CrudRepository<Ticket, Integer> {
    List<Ticket> findAvailableTicketsBySession(Integer sessionId);
    List<Integer> findOccupiedSeatIdsBySessionId(Integer sessionId);
}