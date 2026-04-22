package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.CompetenceView;
import edu.mns.cda.clubcaninbackend.view.SeanceView;
import edu.mns.cda.clubcaninbackend.view.TypeSeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TypeSeanceCompetence {

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Key implements Serializable {

        @Column(name = "id_type_seance")
        protected Integer typeSeanceId;

        @Column(name = "id_competence")
        protected Integer competenceId;
    }

    @EmbeddedId
    protected Key id;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @ManyToOne
    @MapsId("typeSeanceId")
    @JoinColumn(name = "id_type_seance")
    @JsonView(CompetenceView.class)
    protected TypeSeance typeSeance;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @ManyToOne
    @MapsId("competenceId")
    @JoinColumn(name = "id_competence")
    @JsonView(TypeSeanceView.class)
    protected Competence competence;

    @NotNull(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @JsonView({TypeSeanceView.class, CompetenceView.class})
    protected NiveauCompetence niveauMinimumRequis;
}
