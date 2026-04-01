package org.gletchick.lab2.repository;

import java.util.List;

public interface Repository<T, ID> {
    List<T> findAll();

    T findById(ID id);
}