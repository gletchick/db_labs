package org.gletchick.db.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.gletchick.db.model.*;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure();

            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Hall.class);
            configuration.addAnnotatedClass(Seat.class);
            configuration.addAnnotatedClass(Spectacle.class);
            configuration.addAnnotatedClass(Session.class);
            configuration.addAnnotatedClass(Ticket.class);

            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}