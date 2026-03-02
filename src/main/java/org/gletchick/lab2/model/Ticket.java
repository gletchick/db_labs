package org.gletchick.lab2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {
    private int id;
    private int idSession;
    private int idSeat;
    private int idClient;
    private double price;
    private String status;

    public Ticket(Ticket other) {
        this.id = other.id;
        this.idSession = other.idSession;
        this.idSeat = other.idSeat;
        this.idClient = other.idClient;
        this.price = other.price;
        this.status = other.status;
    }
}
