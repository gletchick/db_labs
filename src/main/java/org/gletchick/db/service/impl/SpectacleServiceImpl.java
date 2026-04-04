package org.gletchick.db.service.impl;

import lombok.RequiredArgsConstructor;
import org.gletchick.db.model.Spectacle;
import org.gletchick.db.repository.CrudRepository;
import org.gletchick.db.repository.SpectacleRepository;
import org.gletchick.db.service.SpectacleService;

@RequiredArgsConstructor
public class SpectacleServiceImpl extends BaseService<Spectacle, Integer> implements SpectacleService {

    private final SpectacleRepository spectacleRepository;

    @Override
    protected CrudRepository<Spectacle, Integer> getRepository() {
        return spectacleRepository;
    }
}