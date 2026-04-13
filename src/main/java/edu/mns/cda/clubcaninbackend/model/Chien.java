package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.SeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Chien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(ChienView.class)
    protected Integer id;

    @Column(nullable = false, unique = true)
    @NotBlank(groups = ValidationGroupe.OnCreate.class)
    @JsonView({ChienView.class, SeanceView.class})
    protected String nom;

    @NotNull
    @Min(value = 1, message = "Le poid doit être positif")
    @JsonView({ChienView.class, SeanceView.class})
    @Column(nullable = false)
    protected Double poids;

    @NotNull
    @Min(value = 1, message = "La taille doit être positive")
    @JsonView({ChienView.class, SeanceView.class})
    @Column(nullable = false)
    protected Double taille;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonView(ChienView.class)
    @Column(nullable = false)
    protected Sexe sexe;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonView(ChienView.class)
    @Column(nullable = false)
    protected LocalDate dateNaissance;

    @JsonView(ChienView.class)
    @Size(max = 30, message = "Le numéro de puce ne peut pas depasser 30 caractères")
    protected String numeroPuce;
}
