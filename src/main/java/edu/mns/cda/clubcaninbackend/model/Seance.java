package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.CompetenceView;
import edu.mns.cda.clubcaninbackend.view.SeanceView;
import edu.mns.cda.clubcaninbackend.view.TypeSeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


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
    @FutureOrPresent(groups = ValidationGroupe.OnCreate.class)
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonView(SeanceView.class)
    protected LocalDate date;

    @NotNull
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm:ss")
    @JsonView(SeanceView.class)
    protected LocalTime heureDebut;

    @NotNull
    @Column(nullable = false)
    @Min(value = 1, message = "La durée doit être positive")
    @JsonView(SeanceView.class)
    protected Integer dureeMinutes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonView(SeanceView.class)
    protected StatutSeance statut;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_type_seance", nullable = false)
    @JsonView(SeanceView.class)
    protected TypeSeance typeSeance;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_coach", nullable = false)
    @JsonView(SeanceView.class)
    protected Utilisateur coach;

    @OneToMany(mappedBy = "seance")
    @JsonView(SeanceView.class)
    protected List<Inscription> inscriptions;
}
