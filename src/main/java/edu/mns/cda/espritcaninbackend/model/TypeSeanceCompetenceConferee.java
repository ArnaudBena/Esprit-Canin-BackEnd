package edu.mns.cda.espritcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.view.CompetenceView;
import edu.mns.cda.espritcaninbackend.view.TypeSeanceView;
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
public class TypeSeanceCompetenceConferee {

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

    @NotNull
    @ManyToOne
    @MapsId("typeSeanceId")
    @JoinColumn(name = "id_type_seance")
    @JsonView(CompetenceView.class)
    protected TypeSeance typeSeance;

    @NotNull
    @ManyToOne
    @MapsId("competenceId")
    @JoinColumn(name = "id_competence")
    @JsonView(TypeSeanceView.class)
    protected Competence competence;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @JsonView({TypeSeanceView.class, CompetenceView.class})
    protected NiveauCompetence niveauConfere;
}
