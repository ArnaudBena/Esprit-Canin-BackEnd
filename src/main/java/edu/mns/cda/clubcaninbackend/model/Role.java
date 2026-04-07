package edu.mns.cda.clubcaninbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @Column(length = 20,nullable = false, unique = true)
    @NotBlank
    @Length(min = 3, max = 30)
//    @JsonView(UtilisateurView.class)  << future implémentation de la classe Utilisateur
    protected String nom;

}
