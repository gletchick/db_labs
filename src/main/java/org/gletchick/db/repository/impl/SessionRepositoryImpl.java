package org.gletchick.db.repository.impl;

import org.gletchick.db.model.Session;
import org.gletchick.db.repository.BaseRepositoryImpl;
import org.gletchick.db.repository.SessionRepository;

public class SessionRepositoryImpl extends BaseRepositoryImpl<Session, Integer> implements SessionRepository {
    public SessionRepositoryImpl() {
        super(Session.class);
    }
}