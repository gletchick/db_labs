package org.gletchick.db.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.gletchick.db.dto.PopularityDTO;
import org.gletchick.db.model.*;
import org.gletchick.db.repository.CrudRepository;
import org.gletchick.db.repository.TicketRepository;
import org.gletchick.db.repository.impl.TicketRepositoryImpl;
import org.gletchick.db.service.TicketService;
import org.gletchick.db.util.DbManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TicketServiceImpl extends BaseService<Ticket, Integer> implements TicketService {

    private final TicketRepository ticketRepository;
    private final EntityManager entityManager;

    @Override
    protected CrudRepository<Ticket, Integer> getRepository() {
        return ticketRepository;
    }

    @Override
    public List<Ticket> findByClient(Client client) {
        if (client == null) return Collections.emptyList();
        return findAll().stream()
                .filter(t -> t.getClient() != null && t.getClient().getId().equals(client.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public void purchaseTicket(Integer ticketId, Client client) {
        EntityManager em = DbManager.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Ticket ticket = em.find(Ticket.class, ticketId);

            if (ticket == null) {
                throw new RuntimeException("Билет не найден");
            }

            if (ticket.getStatus() != TicketStatus.AVAILABLE) {
                throw new IllegalStateException("Билет не доступен для покупки. Текущий статус: " + ticket.getStatus());
            }

            ticket.setClient(client);
            ticket.setStatus(TicketStatus.SOLD);

            em.merge(ticket);

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw new RuntimeException("Ошибка при транзакции покупки: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Ticket> findAvailableBySession(Integer sessionId) {
        if (sessionId == null) return Collections.emptyList();
        return ticketRepository.findAvailableTicketsBySession(sessionId);
    }

    @Override
    public void processBooking(Session session, Seat seat, Client client, TicketStatus status) {
        EntityManager em = DbManager.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            Long count = em.createQuery(
                            "SELECT count(t) FROM Ticket t WHERE t.session.id = :sessionId " +
                                    "AND t.seat.id = :seatId AND t.status IN (:statuses)", Long.class)
                    .setParameter("sessionId", session.getIdSession())
                    .setParameter("seatId", seat.getIdSeat())
                    .setParameter("statuses", List.of(TicketStatus.SOLD, TicketStatus.BOOKED))
                    .getSingleResult();

            if (count > 0) {
                throw new IllegalStateException("Это место уже занято другим пользователем!");
            }

            Ticket ticket = new Ticket();
            ticket.setSession(session);
            ticket.setSeat(seat);
            ticket.setClient(client);
            ticket.setStatus(status);
            ticket.setPrice(15.0);

            em.persist(ticket);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Set<Integer> findOccupiedSeatIdsBySession(Integer sessionId) {
        if (sessionId == null) return Collections.emptySet();
        return new java.util.HashSet<>(((TicketRepository) getRepository()).findOccupiedSeatIdsBySessionId(sessionId));
    }

    @Override
    public List<PopularityDTO> getPopularityData(LocalDateTime start, LocalDateTime end) {
        String jpql = "SELECT new org.gletchick.db.dto.PopularityDTO(t.session.spectacle.title, COUNT(t.id)) " +
                "FROM Ticket t " +
                "WHERE t.session.dateTimeStart BETWEEN :start AND :end " +
                "AND t.status = :status " +
                "GROUP BY t.session.spectacle.title " +
                "ORDER BY COUNT(t.id) DESC";

        TypedQuery<PopularityDTO> query = entityManager.createQuery(jpql, PopularityDTO.class);
        query.setParameter("start", start);
        query.setParameter("end", end);
        query.setParameter("status", TicketStatus.SOLD);

        return query.getResultList();
    }
}