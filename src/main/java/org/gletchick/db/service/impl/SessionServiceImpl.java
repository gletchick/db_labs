package org.gletchick.db.service.impl;

import lombok.RequiredArgsConstructor;
import org.gletchick.db.model.Session;
import org.gletchick.db.repository.CrudRepository;
import org.gletchick.db.repository.SessionRepository;
import org.gletchick.db.repository.impl.SessionRepositoryImpl;
import org.gletchick.db.service.SessionService;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class SessionServiceImpl extends BaseService<Session, Integer> implements SessionService {
    private final SessionRepository repository;

    @Override
    protected CrudRepository<Session, Integer> getRepository() {
        return repository;
    }

    @Override
    public List<Session> findBySpectacle(Integer spectacleId) {
        if (spectacleId == null) return Collections.emptyList();
        return ((SessionRepository) getRepository()).findBySpectacleId(spectacleId);
    }
}