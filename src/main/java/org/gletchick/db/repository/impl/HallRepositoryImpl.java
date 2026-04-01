package org.gletchick.db.repository.impl;

import org.gletchick.db.model.Hall;
import org.gletchick.db.repository.BaseRepositoryImpl;
import org.gletchick.db.repository.HallRepository;

public class HallRepositoryImpl extends BaseRepositoryImpl<Hall, Integer> implements HallRepository {
    public HallRepositoryImpl() {
        super(Hall.class);
    }
}