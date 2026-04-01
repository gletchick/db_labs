package org.gletchick.db.service;

import java.util.List;
import java.util.Optional;

public interface Service<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    T update(T entity);
    void deleteById(ID id);
}