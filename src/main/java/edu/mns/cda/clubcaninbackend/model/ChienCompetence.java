package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.CompetenceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChienCompetence {

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Key implements Serializable {

        @Column(name = "id_chien")
        protected Integer chienId;

        @Column(name = "id_competence")
        protected Integer competenceId;

    }

    @EmbeddedId
    protected Key id;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @ManyToOne
    @MapsId("chienId")
    @JoinColumn(name = "id_chien")
    protected Chien chien;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @ManyToOne
    @MapsId("competenceId")
    @JoinColumn(name = "id_competence")
    @JsonView({ChienView.class, CompetenceView.class})
    protected Competence competence;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @JsonView({ChienView.class, CompetenceView.class})
    protected NiveauCompetence niveauActuel;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    @JsonView({ChienView.class, CompetenceView.class})
    protected LocalDate dateAcquisition;
}
