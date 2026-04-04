package org.gletchick.db.service.impl;

import lombok.RequiredArgsConstructor;
import org.gletchick.db.model.Hall;
import org.gletchick.db.repository.CrudRepository;
import org.gletchick.db.repository.HallRepository;
import org.gletchick.db.service.HallService;

@RequiredArgsConstructor
public class HallServiceImpl extends BaseService<Hall, Integer> implements HallService {
    private final HallRepository repository;

    @Override
    protected CrudRepository<Hall, Integer> getRepository() {
        return repository;
    }
}