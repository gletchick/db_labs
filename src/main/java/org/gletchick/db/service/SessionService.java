package org.gletchick.db.service;

import org.gletchick.db.model.Session;

import java.util.List;

public interface SessionService extends Service<Session, Integer> {
    List<Session> findBySpectacle(Integer spectacleId);
}