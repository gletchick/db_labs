package org.gletchick.db.repository.impl;

import org.gletchick.db.model.Spectacle;
import org.gletchick.db.repository.SpectacleRepository;

public class SpectacleRepositoryImpl extends BaseRepositoryImpl<Spectacle, Integer> implements SpectacleRepository {
    public SpectacleRepositoryImpl() {
        super(Spectacle.class);
    }
}