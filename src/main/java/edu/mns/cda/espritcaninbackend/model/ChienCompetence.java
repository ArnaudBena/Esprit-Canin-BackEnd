package edu.mns.cda.espritcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.view.ChienView;
import edu.mns.cda.espritcaninbackend.view.CompetenceView;
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

    @NotNull
    @ManyToOne
    @MapsId("chienId")
    @JoinColumn(name = "id_chien")
    protected Chien chien;

    @NotNull
    @ManyToOne
    @MapsId("competenceId")
    @JoinColumn(name = "id_competence")
    @JsonView({ChienView.class, CompetenceView.class})
    protected Competence competence;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @JsonView({ChienView.class, CompetenceView.class})
    protected NiveauCompetence niveauActuel;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    @JsonView({ChienView.class, CompetenceView.class})
    protected LocalDate dateAcquisition;
}
