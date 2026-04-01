package org.gletchick.db.service.impl;

import org.gletchick.db.repository.CrudRepository;
import org.gletchick.db.service.Service;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<T, ID> implements Service<T, ID> {

    protected abstract CrudRepository<T, ID> getRepository();

    @Override
    public T save(T entity) {
        return getRepository().save(entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }

    @Override
    public List<T> findAll() {
        return getRepository().findAll();
    }

    @Override
    public T update(T entity) {
        return getRepository().update(entity);
    }

    @Override
    public void deleteById(ID id) {
        getRepository().deleteById(id);
    }
}