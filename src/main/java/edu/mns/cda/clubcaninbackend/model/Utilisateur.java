package edu.mns.cda.clubcaninbackend.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.UtilisateurView;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(UtilisateurView.class)
    protected Integer id;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Max(value = 50, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @JsonView(UtilisateurView.class)
    protected String nom;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Max(value = 50, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @JsonView(UtilisateurView.class)
    protected String prenom;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Max(value = 100, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @JsonView(UtilisateurView.class)
    protected String email;

    @NotBlank
    @Column(nullable = false, unique = true)
    @Min(value = 8, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class}, message = "Le mot de passe ne peut pas faire moins de 8 caractères")
    protected String password;

    @NotNull
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonView(UtilisateurView.class)
    protected LocalDate dateInscription;

    @Size(max = 20, message = "Le numéro de téléphone est trop long")
    @JsonView(UtilisateurView.class)
    protected String telephone;
}
