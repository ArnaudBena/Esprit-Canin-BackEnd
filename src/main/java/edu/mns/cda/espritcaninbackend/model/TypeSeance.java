package edu.mns.cda.espritcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.espritcaninbackend.view.ChienView;
import edu.mns.cda.espritcaninbackend.view.SeanceView;
import edu.mns.cda.espritcaninbackend.view.TypeSeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TypeSeance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({TypeSeanceView.class, SeanceView.class, ChienView.class})
    protected Integer id;

    @Column(nullable = false, unique = true) // peut être enlever le unique
    @NotBlank(message = "Le libellé de la séance ne peut pas être vide")
    @JsonView({TypeSeanceView.class, SeanceView.class, ChienView.class})
    protected String libelle;

    @Column(columnDefinition = "TEXT")
    @JsonView({TypeSeanceView.class,SeanceView.class})
    protected String description;

    @Min(value = 0, message = "L'âge minimum ne peut pas être négatif")
    @JsonView({TypeSeanceView.class,SeanceView.class})
    protected Integer ageMinimumMois;

    @Max(value = 360, message = "L'âge maximum ne peut pas dépasser 30 ans (360 mois)")
    @JsonView({TypeSeanceView.class,SeanceView.class})
    protected Integer ageMaximumMois;

    @NotNull
    @Column(nullable = false)
    @Min(value = 30,message = "La durée minimale ne peut pas être inférieure à 30 mins")
    @JsonView(TypeSeanceView.class)
    protected Integer dureeMinimale;

    @NotNull
    @Column(nullable = false)
    @Max(value = 240, message = "La durée maximale ne peut pas être supérieure à 4h (240 mins)")
    @JsonView(TypeSeanceView.class)
    protected Integer dureeMaximale;

    @NotNull
    @Column(nullable = false)
    @Min(value = 1, message = "Le nombre de participants minimum ne peut pas être inférieur à 1")
    @JsonView(TypeSeanceView.class)
    protected Integer participantsMinimum;

    @NotNull
    @Column(nullable = false)
    @Max(value = 10, message = "Le nombre de participants maximum ne peut pas être supérieur à 10")
    @JsonView({TypeSeanceView.class,SeanceView.class})
    protected Integer participantsMaximum;

    @OneToMany(mappedBy = "typeSeance")
    @JsonView(TypeSeanceView.class)
    protected List<TypeSeanceCompetence> typeSeancesCompetences;

    @OneToMany(mappedBy = "typeSeance")
    @JsonView(TypeSeanceView.class)
    protected List<TypeSeanceCompetenceConferee> typeSeancesCompetencesConferees;
}
