package org.gletchick.lab2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    private int id;
    private String phone;
    private String name;
    private String surname;
    private String patronymic;

    public Client(Client other) {
        this.id = other.id;
        this.phone = other.phone;
        this.name = other.name;
        this.surname = other.surname;
        this.patronymic = other.patronymic;
    }
}