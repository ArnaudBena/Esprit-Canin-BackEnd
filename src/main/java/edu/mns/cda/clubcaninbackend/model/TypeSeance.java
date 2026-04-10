package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.SeanceView;
import edu.mns.cda.clubcaninbackend.view.TypeSeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TypeSeance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(TypeSeanceView.class)
    protected Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "Le libellé du cours ne peut pas être vide")
    @JsonView({TypeSeanceView.class, SeanceView.class})
    protected String libelle;

    @Column(columnDefinition = "TEXT")
    @JsonView(TypeSeanceView.class)
    protected String description;

    @Min(value = 0, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "L'âge minimum ne peut pas être négatif")
    @JsonView(TypeSeanceView.class)
    protected int ageMinimum;

    @Max(value = 30, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "L'âge maximum ne peut pas dépasser 30 ans")
    @JsonView(TypeSeanceView.class)
    protected int ageMaximum;

    @Min(value = 30,groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "La durée minimale ne peut pas être inférieure à 30 mins")
    @JsonView(TypeSeanceView.class)
    protected int dureeMinimale;

    @Max(value = 240, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "La durée maximale ne peut pas être supérieure à 4h")
    @JsonView(TypeSeanceView.class)
    protected int dureeMaximale;

    @Min(value = 1, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "Le nombre de participants minimum ne peut pas être inférieur à 1")
    @JsonView({TypeSeanceView.class, SeanceView.class})
    protected int participantsMinimum;

    @Max(value = 10, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "Le nombre de participants maximum ne peut pas être supérieur à 10")
    @JsonView(TypeSeanceView.class)
    protected int participantsMaximum;
}
