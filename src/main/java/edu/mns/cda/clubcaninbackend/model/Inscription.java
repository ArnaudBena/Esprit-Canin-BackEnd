package edu.mns.cda.clubcaninbackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import edu.mns.cda.clubcaninbackend.view.ChienView;
import edu.mns.cda.clubcaninbackend.view.SeanceView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Inscription {

    @Embeddable
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Key implements Serializable {
        @Column(name = "id_chien")
        protected Integer chienId;

        @Column(name = " id_seance")
        protected Integer seanceId;
    }

    @EmbeddedId
    protected Key id;

    @NotNull
    @ManyToOne
    @MapsId("chienId")
    @JoinColumn(name = "id_chien")
    @JsonView(SeanceView.class)
    protected Chien chien;

    @NotNull
    @ManyToOne
    @MapsId("seanceId")
    @JoinColumn(name = "id_seance")
    @JsonView(ChienView.class)
    protected Seance seance;

    @NotNull
    @CreatedDate
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false, updatable = false)
    @JsonView({ChienView.class, SeanceView.class})
    protected LocalDate dateInscription;

    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    @Column(length = 500)
    @JsonView(SeanceView.class)
    protected String commentaireCoach;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @JsonView({ChienView.class, SeanceView.class})
    protected StatutPresence statutPresence;

    @NotNull
    @Column(nullable = false)
    @JsonView({ChienView.class, SeanceView.class})
    protected Boolean acquisitionValidee = false;
}
