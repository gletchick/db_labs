package org.gletchick.db.repository.impl;

import org.gletchick.db.model.Client;
import org.gletchick.db.repository.ClientRepository;

public class ClientRepositoryImpl extends BaseRepositoryImpl<Client, Integer> implements ClientRepository {
    public ClientRepositoryImpl() {
        super(Client.class);
    }
}