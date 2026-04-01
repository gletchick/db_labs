package org.gletchick.lab2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sessions")
@AllArgsConstructor
@NoArgsConstructor
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Integer idSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_spectacle")
    private Spectacle spectacle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hall")
    private Hall hall;

    @Column(name = "date_time_start")
    private LocalDateTime dateTimeStart;
}