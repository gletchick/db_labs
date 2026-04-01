package org.gletchick.db.service;

import org.gletchick.db.model.Client;

import java.util.Optional;

public interface ClientService extends Service<Client, Integer> {
    Optional<Client> findByPhone(String phone);
    Optional<Client> authenticate(String login, String password);
}