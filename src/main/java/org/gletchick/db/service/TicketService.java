package org.gletchick.db.service;

import org.gletchick.db.dto.PopularityDTO;
import org.gletchick.db.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface TicketService extends Service<Ticket, Integer> {

    List<Ticket> findByClient(Client client);

    void purchaseTicket(Integer ticketId, Client client);
    List<Ticket> findAvailableBySession(Integer sessionId);
    void processBooking(Session session, Seat seat, Client client, TicketStatus status);
    Set<Integer> findOccupiedSeatIdsBySession(Integer sessionId);
    List<PopularityDTO> getPopularityData(LocalDateTime start, LocalDateTime end);
}