package org.gletchick.lab2.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.gletchick.lab2.db.DbManager;
import org.gletchick.lab2.model.Client;
import org.gletchick.lab2.model.EntityState;
import org.gletchick.lab2.model.RowState;
import org.gletchick.lab2.model.Ticket;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ClientRepository {
    private final DbManager dbManager = new DbManager();

    @Getter
    private final ObservableList<EntityState<Client>> clientStates = FXCollections.observableArrayList();
    @Getter
    private final ObservableList<EntityState<Ticket>> ticketStates = FXCollections.observableArrayList();

    public void loadFromDb() throws SQLException {
        // Загружаем клиентов
        clientStates.setAll(dbManager.readTableClients().stream()
                .map(c -> new EntityState<>(c, RowState.UNCHANGED))
                .collect(Collectors.toList()));

        // Загружаем билеты (чтобы было что показывать в иерархии)
        ticketStates.setAll(dbManager.readTableTickets().stream()
                .map(t -> new EntityState<>(t, RowState.UNCHANGED))
                .collect(Collectors.toList()));
    }

    // Метод для связи Master-Detail
    public List<Ticket> getTicketsByClientId(int clientId) {
        return ticketStates.stream()
                .filter(ts -> ts.getState() != RowState.DELETED)
                .map(EntityState::getEntity)
                .filter(t -> t.getIdClient() == clientId)
                .collect(Collectors.toList());
    }

    // Остальные методы (addLocally, markDeleted, saveChanges) остаются...
    public void saveChanges() throws SQLException {
        dbManager.syncAll(clientStates, ticketStates); // Сохраняем и билеты тоже
        loadFromDb();
    }


    // Добавление (Пункт 3 лабы - Create/Insert)
    public void addLocally(Client client) {
        // Создаем обертку со статусом ADDED (аналог row.RowState = Added)
        clientStates.add(new EntityState<>(client, RowState.ADDED));
    }

    // Удаление (Пункт 3 лабы - Delete)
    public void markDeleted(EntityState<Client> state) {
        if (state.getState() == RowState.ADDED) {
            // Если запись еще не в БД, просто убираем из списка
            clientStates.remove(state);
        } else {
            // Если в БД есть — помечаем DELETED (аналог row.Delete())
            state.setState(RowState.DELETED);
        }
    }

    public void rejectChanges(EntityState<Client> state) {
        if (state.getState() == RowState.ADDED) {
            clientStates.remove(state);
        } else {
            state.rejectChanges(); // Метод в EntityState, который восстанавливает originalEntity
        }
    }
}