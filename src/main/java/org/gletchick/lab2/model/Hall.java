package org.gletchick.lab2.model;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Hall {
    private int idHall;
    private String hallName;
    private int capacity;
}