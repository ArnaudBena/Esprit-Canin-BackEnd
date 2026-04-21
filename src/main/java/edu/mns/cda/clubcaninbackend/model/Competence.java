package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.CompetenceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({ChienView.class, CompetenceView.class})
    protected Integer id;

    @NotBlank(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @Column(length = 50, nullable = false, unique = true)
    @Length(min = 3, max = 50, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @JsonView({ChienView.class, CompetenceView.class})
    protected String nom;

    @Column(columnDefinition = "TEXT")
    @JsonView({ChienView.class, CompetenceView.class})
    protected String description;

    @OneToMany(mappedBy = "competence")
    protected List<ChienCompetence> chienCompetences;

    @OneToMany(mappedBy = "competence")
    protected List<TypeSeanceCompetence> typeSeancesCompetences;

    @OneToMany(mappedBy = "competence")
    protected List<TypeSeanceCompetenceConferee> typeSeancesCompetencesConferees;
}
