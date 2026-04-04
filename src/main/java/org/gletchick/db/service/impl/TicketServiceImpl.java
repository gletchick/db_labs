package org.gletchick.db.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.gletchick.db.model.*;
import org.gletchick.db.repository.CrudRepository;
import org.gletchick.db.repository.TicketRepository;
import org.gletchick.db.repository.impl.TicketRepositoryImpl;
import org.gletchick.db.service.TicketService;
import org.gletchick.db.util.DbManager;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TicketServiceImpl extends BaseService<Ticket, Integer> implements TicketService {

    private final TicketRepository ticketRepository;

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

            // 1. Загружаем билет в контекст текущего EntityManager
            Ticket ticket = em.find(Ticket.class, ticketId);

            if (ticket == null) {
                throw new RuntimeException("Билет не найден");
            }

            // 2. Проверка статуса через Enum
            if (ticket.getStatus() != TicketStatus.AVAILABLE) {
                throw new IllegalStateException("Билет не доступен для покупки. Текущий статус: " + ticket.getStatus());
            }

            // 3. Обновляем данные объекта
            ticket.setClient(client);
            ticket.setStatus(TicketStatus.SOLD); // Используем Enum

            // 4. Фиксируем изменения (merge обновит запись в БД при commit)
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
        // Вызываем кастомный метод репозитория, который мы обсудили ранее
        return ((TicketRepositoryImpl) ticketRepository).findAvailableTicketsBySession(sessionId);
    }

    @Override
    public void processBooking(Session session, Seat seat, Client client, TicketStatus status) {
        EntityManager em = DbManager.getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            // Проверяем, не существует ли уже проданный/забронированный билет на это место/сеанс
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

            // Если место свободно, создаем билет
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
}