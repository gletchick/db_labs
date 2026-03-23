package org.gletchick.lab2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Spectacle {
    private int idSpectacle;
    private String title;
    private String genre;
    private int duration;
}