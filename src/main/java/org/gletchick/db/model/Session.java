package org.gletchick.db.model;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_spectacle")
    private Spectacle spectacle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_hall")
    private Hall hall;

    @Column(name = "date_time_start")
    private LocalDateTime dateTimeStart;

    @Override
    public String toString() {
        if (spectacle == null || dateTimeStart == null) return "Некорректный сеанс";

        java.util.Locale russian = new java.util.Locale("ru");
        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd MMM, HH:mm", russian);

        return spectacle.getTitle() + " | " + dateTimeStart.format(formatter);
    }
}