package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.view.RoleView;
import edu.mns.cda.clubcaninbackend.view.UtilisateurView;
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
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({UtilisateurView.class, RoleView.class})
    protected Integer id;

    @NotBlank
    @Column(length = 30,nullable = false, unique = true)
    @Length(min = 3, max = 30)
    @JsonView({UtilisateurView.class, RoleView.class})
    protected String nom;

    @Column(columnDefinition = "TEXT")
    @JsonView({UtilisateurView.class, RoleView.class})
    protected String description;

    @OneToMany(mappedBy = "role")
    protected List<Utilisateur> utilisateurs;

}
