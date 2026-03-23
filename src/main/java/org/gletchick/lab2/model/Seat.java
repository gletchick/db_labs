package org.gletchick.lab2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Seat {
    private int idSeat;
    private int idHall;
    private int rowNumber;
    private int seatNumber;
}