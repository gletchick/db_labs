package org.gletchick.db.factory;

import jakarta.persistence.EntityManager;
import org.gletchick.db.model.*;
import org.gletchick.db.repository.*;
import org.gletchick.db.repository.impl.*;
import org.gletchick.db.service.*;
import org.gletchick.db.service.impl.*;
import org.gletchick.db.util.DbManager;
import org.gletchick.db.util.HibernateUtil;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {

    private static ServiceFactory instance;

    private final SpectacleService spectacleService;
    private final TicketService ticketService;
    private final HallService hallService;
    private final SeatService seatService;
    private final SessionService sessionService;
    private final ClientService clientService;

    private final Map<Class<?>, Service<?, Integer>> serviceMap = new HashMap<>();

    private ServiceFactory() {
        EntityManager entityManager = DbManager.getEntityManager();

        SpectacleRepository spectacleRepository = new SpectacleRepositoryImpl();
        TicketRepository ticketRepository = new TicketRepositoryImpl();
        HallRepository hallRepository = new HallRepositoryImpl();
        SeatRepository seatRepository = new SeatRepositoryImpl();
        SessionRepository sessionRepository = new SessionRepositoryImpl();
        ClientRepository clientRepository = new ClientRepositoryImpl();

        this.spectacleService = new SpectacleServiceImpl(spectacleRepository);
        this.ticketService = new TicketServiceImpl(ticketRepository, entityManager);
        this.hallService = new HallServiceImpl(hallRepository);
        this.seatService = new SeatServiceImpl(seatRepository);
        this.sessionService = new SessionServiceImpl(sessionRepository);
        this.clientService = new ClientServiceImpl(clientRepository);

        serviceMap.put(Spectacle.class, spectacleService);
        serviceMap.put(Ticket.class, ticketService);
        serviceMap.put(Hall.class, hallService);
        serviceMap.put(Seat.class, seatService);
        serviceMap.put(Session.class, sessionService);
        serviceMap.put(Client.class, clientService);
    }

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public SpectacleService getSpectacleService() { return spectacleService; }
    public TicketService getTicketService() { return ticketService; }
    public HallService getHallService() { return hallService; }
    public SeatService getSeatService() { return seatService; }
    public SessionService getSessionService() { return sessionService; }
    public ClientService getClientService() { return clientService; }
}