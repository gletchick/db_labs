package org.gletchick.db.repository.impl;

import org.gletchick.db.model.Ticket;
import org.gletchick.db.repository.BaseRepositoryImpl;
import org.gletchick.db.repository.TicketRepository;

public class TicketRepositoryImpl extends BaseRepositoryImpl<Ticket, Integer> implements TicketRepository {

    public TicketRepositoryImpl() {
        super(Ticket.class);
    }
}