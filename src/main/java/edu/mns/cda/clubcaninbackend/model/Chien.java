package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.SeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

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

    @NotBlank
    @Column(nullable = false, length = 50)
    @JsonView({ChienView.class, SeanceView.class})
    protected String nom;

    @NotNull
    @Column(nullable = false)
    @Positive(message = "Le poids doit être positif")
    @JsonView({ChienView.class, SeanceView.class})
    protected Double poids;

    @NotNull
    @Column(nullable = false)
    @Positive(message = "La taille doit être positive")
    @JsonView({ChienView.class, SeanceView.class})
    protected Double taille;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonView(ChienView.class)
    protected Sexe sexe;

    @NotNull
    @PastOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false, updatable = false)
    @JsonView(ChienView.class)
    protected LocalDate dateNaissance;

    @Column(unique = true, length = 30)
    @Size(max = 30, message = "Le numéro de puce ne peut pas depasser 30 caractères")
    @JsonView(ChienView.class)
    protected String numeroPuce;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    @JsonView(ChienView.class)
    protected Utilisateur utilisateur;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_race", nullable = false)
    @JsonView(ChienView.class)
    protected Race race;

    @OneToMany(mappedBy = "chien")
    @JsonView(ChienView.class)
    protected List<ChienCompetence> chienCompetences;

    @OneToMany(mappedBy = "chien")
    @JsonView(ChienView.class)
    protected List<Inscription> inscriptions;
}
