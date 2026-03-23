package org.gletchick.lab2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Session {
    private int idSession;
    private int idSpectacle;
    private int idHall;
    private LocalDateTime dateTimeStart;
}