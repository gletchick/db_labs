package org.gletchick.db.service.impl;

import lombok.RequiredArgsConstructor;
import org.gletchick.db.model.Client;
import org.gletchick.db.repository.ClientRepository;
import org.gletchick.db.repository.CrudRepository;
import org.gletchick.db.repository.impl.ClientRepositoryImpl;
import org.gletchick.db.service.ClientService;

import java.util.Optional;

@RequiredArgsConstructor
public class ClientServiceImpl extends BaseService<Client, Integer> implements ClientService {

    private final ClientRepository repository;

    @Override
    protected CrudRepository<Client, Integer> getRepository() {
        return repository;
    }

    @Override
    public Optional<Client> findByPhone(String phone) {
        return findAll().stream()
                .filter(c -> c.getPhone().equals(phone))
                .findFirst();
    }

    @Override
    public Optional<Client> authenticate(String login, String password) {
        return findAll().stream()
                .filter(c -> login.equals(c.getLogin()) && password.equals(c.getPassword()))
                .findFirst();
    }
}