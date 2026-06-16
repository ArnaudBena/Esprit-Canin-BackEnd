package edu.mns.cda.espritcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.espritcaninbackend.view.ChienView;
import edu.mns.cda.espritcaninbackend.view.InscriptionView;
import edu.mns.cda.espritcaninbackend.view.SeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @JsonView({SeanceView.class, InscriptionView.class, ChienView.class})
    protected Integer id;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @FutureOrPresent(groups = ValidationGroupe.OnCreate.class)
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonView({SeanceView.class,InscriptionView.class, ChienView.class})
    protected LocalDate date;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm:ss")
    @JsonView({SeanceView.class,InscriptionView.class, ChienView.class})
    protected LocalTime heureDebut;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @Column(nullable = false)
    @Min(value = 1, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "La durée doit être positive")
    @JsonView({SeanceView.class, InscriptionView.class})
    protected Integer dureeMinutes;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonView({SeanceView.class, ChienView.class})
    protected StatutSeance statut;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @ManyToOne
    @JoinColumn(name = "id_type_seance", nullable = false)
    @JsonView({SeanceView.class, ChienView.class, InscriptionView.class})
    protected TypeSeance typeSeance;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @ManyToOne
    @JoinColumn(name = "id_coach", nullable = false)
    @JsonView({SeanceView.class, InscriptionView.class})
    protected Utilisateur coach;

    @OneToMany(mappedBy = "seance", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonView(SeanceView.class)
    protected List<Inscription> inscriptions;

    @JsonView({SeanceView.class, InscriptionView.class})
    public Boolean getComplet() {
        if (inscriptions == null || typeSeance == null || typeSeance.getParticipantsMaximum() == null) {
            return false;
        }
        long actives = inscriptions.stream()
                .filter(i -> i.getStatutPresence() != StatutPresence.ANNULEE)
                .count();
        return actives >= typeSeance.getParticipantsMaximum();
    }

    @JsonView({SeanceView.class, InscriptionView.class, ChienView.class})
    public Boolean getTerminee() {
        if (date == null || heureDebut == null || dureeMinutes == null) return false;
        // terminée = on a dépassé la fin réelle (date + heure de début + durée)
        return LocalDateTime.now().isAfter(date.atTime(heureDebut).plusMinutes(dureeMinutes));
    }
}
