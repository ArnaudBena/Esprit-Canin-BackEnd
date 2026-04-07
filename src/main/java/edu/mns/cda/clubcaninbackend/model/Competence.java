package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.view.ChienView;
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
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView({ChienView.class})
    protected Integer id;

    @Column(length = 50,nullable = false, unique = true)
    @NotBlank
    @Length(min = 3, max = 50)
    @JsonView({ChienView.class})
    protected String nom;
}
