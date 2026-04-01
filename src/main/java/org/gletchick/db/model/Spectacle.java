package org.gletchick.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "spectacles")
@AllArgsConstructor
@NoArgsConstructor
public class Spectacle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_spectacle")
    private Integer idSpectacle;

    private String title;
    private String genre;
    private Integer duration;
}