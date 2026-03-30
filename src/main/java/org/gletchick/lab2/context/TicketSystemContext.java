package org.gletchick.lab2.context;

import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TicketSystemContext {
    private final DbManager dbManager;

    private List<EntityState<Client>> clientStates = new ArrayList<>();
    private List<EntityState<Ticket>> ticketStates = new ArrayList<>();
    private List<EntityState<Session>> sessionStates = new ArrayList<>();
    private List<EntityState<Spectacle>> spectacleStates = new ArrayList<>();
    private List<EntityState<Hall>> hallStates = new ArrayList<>();
    private List<EntityState<Seat>> seatStates = new ArrayList<>();

    public TicketSystemContext() {
        this.dbManager = new DbManager();
        loadDataFromDb();
    }

    private void loadDataFromDb() {
        try {
            clientStates = dbManager.readTableClients().stream()
                    .map(c -> new EntityState<>(c, RowState.UNCHANGED))
                    .collect(Collectors.toList());

            ticketStates = dbManager.readTableTickets().stream()
                    .map(t -> new EntityState<>(t, RowState.UNCHANGED))
                    .collect(Collectors.toList());

            sessionStates = dbManager.readTableSessions().stream()
                    .map(s -> new EntityState<>(s, RowState.UNCHANGED))
                    .collect(Collectors.toList());

            spectacleStates = dbManager.readTableSpectacles().stream()
                    .map(s -> new EntityState<>(s, RowState.UNCHANGED))
                    .collect(Collectors.toList());

            hallStates = dbManager.readTableHalls().stream()
                    .map(h -> new EntityState<>(h, RowState.UNCHANGED))
                    .collect(Collectors.toList());

            seatStates = dbManager.readTableSeats().stream()
                    .map(s -> new EntityState<>(s, RowState.UNCHANGED))
                    .collect(Collectors.toList());

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize DataContext", e);
        }
    }

    public void submitChanges() throws SQLException {
        dbManager.syncAll(clientStates, ticketStates, spectacleStates, hallStates, seatStates, sessionStates);
        loadDataFromDb();
    }

    public List<Ticket> getTickets() {
        return ticketStates.stream()
                .filter(ts -> ts.getState() != RowState.DELETED)
                .map(EntityState::getEntity)
                .collect(Collectors.toList());
    }

    public void insertTicket(Ticket ticket) {
        ticketStates.add(new EntityState<>(ticket, RowState.ADDED));
    }

    public void markTicketModified(Ticket ticket) {
        ticketStates.stream()
                .filter(ts -> ts.getEntity().getId() == ticket.getId())
                .findFirst()
                .ifPresent(ts -> {
                    if (ts.getState() != RowState.ADDED) {
                        ts.setState(RowState.MODIFIED);
                    }
                });
    }

    public void deleteTicket(Ticket ticket) {
        ticketStates.stream()
                .filter(ts -> ts.getEntity().getId() == ticket.getId())
                .findFirst()
                .ifPresent(ts -> {
                    if (ts.getState() == RowState.ADDED) {
                        ticketStates.remove(ts);
                    } else {
                        ts.setState(RowState.DELETED);
                    }
                });
    }
}