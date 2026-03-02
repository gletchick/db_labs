package org.gletchick.lab2.model;

import lombok.Getter;
import lombok.Setter;

public class EntityState<T> {
    @Getter @Setter
    private T entity;

    private final T originalEntity;

    @Getter @Setter
    private RowState state;

    public EntityState(T entity, RowState state) {
        this.entity = entity;
        this.state = state;

        // Создаем независимую копию данных
        if (entity instanceof Client) {
            this.originalEntity = (T) new Client((Client) entity);
        } else if (entity instanceof Ticket) {
            this.originalEntity = (T) new Ticket((Ticket) entity);
        } else {
            this.originalEntity = null;
        }
    }

    // Метод отката (аналог RejectChanges)
    public void rejectChanges() {
        if (state == RowState.MODIFIED || state == RowState.DELETED) {
            // Возвращаем данные из оригинала в текущую сущность
            if (entity instanceof Client) {
                copyClientData((Client) originalEntity, (Client) entity);
            } else if (entity instanceof Ticket) {
                copyTicketData((Ticket) originalEntity, (Ticket) entity);
            }
            this.state = RowState.UNCHANGED;
        }
    }

    private void copyClientData(Client source, Client target) {
        target.setName(source.getName());
        target.setSurname(source.getSurname());
        target.setPatronymic(source.getPatronymic());
        target.setPhone(source.getPhone());
    }

    private void copyTicketData(Ticket source, Ticket target) {
        target.setPrice(source.getPrice());
        target.setStatus(source.getStatus());
    }
}