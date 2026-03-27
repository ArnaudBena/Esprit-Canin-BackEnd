package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.RaceView;
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
public class Race {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(RaceView.class)
    protected Integer id;

    @Column(length = 20,nullable = false, unique = true)
    @NotBlank
    @Length(min = 3, max = 30)
    @JsonView({RaceView.class, ChienView.class}) // a confirmer je suis pas sur
    protected String nom;

}
