package org.gletchick.db.repository.impl;

import jakarta.persistence.EntityManager;
import org.gletchick.db.model.Session;
import org.gletchick.db.repository.SessionRepository;
import org.gletchick.db.util.DbManager;

import java.util.List;

public class SessionRepositoryImpl extends BaseRepositoryImpl<Session, Integer> implements SessionRepository {
    public SessionRepositoryImpl() {
        super(Session.class);
    }

    @Override
    public List<Session> findBySpectacleId(Integer spectacleId) {
        EntityManager em = DbManager.getEntityManager();
        try {
            return em.createQuery("SELECT s FROM Session s WHERE s.spectacle.idSpectacle = :id", Session.class)
                    .setParameter("id", spectacleId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}