package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.utile.ValidationGroupe;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.RaceView;
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
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({ChienView.class, RaceView.class})
    protected Integer id;

    @NotBlank(groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @Column(length = 50, nullable = false, unique = true)
    @Length(min = 3, max = 50, groups = {ValidationGroupe.OnCreate.class, ValidationGroupe.OnUpdate.class})
    @JsonView({ChienView.class, RaceView.class})
    protected String nom;

    @OneToMany(mappedBy = "race")
    protected List<Chien> chiens;
}
