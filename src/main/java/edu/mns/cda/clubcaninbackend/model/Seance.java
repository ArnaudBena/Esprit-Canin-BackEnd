package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.view.SeanceView;
import edu.mns.cda.clubcaninbackend.view.TypeSeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Seance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(SeanceView.class)
    protected Integer id;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonView(SeanceView.class)
    @Column(nullable = false)
    protected LocalDateTime date;

    @NotNull
    @JsonFormat(pattern = "HH:mm")
    @JsonView(SeanceView.class)
    @Column(nullable = false)
    protected LocalTime heureDebut;

    @NotNull
    @Min(value = 1, message = "La durée doit être positive")
    @JsonView(SeanceView.class)
    @Column(nullable = false)
    protected Integer dureeMinutes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(SeanceView.class)
    @Column(nullable = false)
    protected StatutSeance statut;

}
