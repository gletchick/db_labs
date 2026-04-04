package org.gletchick.db.repository;

import org.gletchick.db.model.Session;

import java.util.List;

public interface SessionRepository extends CrudRepository<Session, Integer> {
    List<Session> findBySpectacleId(Integer spectacleId);
}