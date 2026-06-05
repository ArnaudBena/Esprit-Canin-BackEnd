package edu.mns.cda.espritcaninbackend.unit.service;

import edu.mns.cda.espritcaninbackend.dao.SeanceDao;
import edu.mns.cda.espritcaninbackend.dao.TypeSeanceDao;
import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.exception.SeanceNotFoundException;
import edu.mns.cda.espritcaninbackend.model.*;
import edu.mns.cda.espritcaninbackend.service.SeanceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests UNITAIRES de {@link SeanceService} : validations métier de création
 * (coach valide, durée dans les bornes du type, pas de chevauchement) et
 * suppression. Les 3 DAO sont mockés (aucune base, aucun contexte Spring).
 */
@ExtendWith(MockitoExtension.class)
class SeanceServiceUnitTest {

    @Mock
    private SeanceDao seanceDao;
    @Mock
    private UtilisateurDao utilisateurDao;
    @Mock
    private TypeSeanceDao typeSeanceDao;

    @InjectMocks
    private SeanceService service;

    private Utilisateur coach(String roleNom) {
        Role role = new Role();
        role.setNom(roleNom);
        Utilisateur u = new Utilisateur();
        u.setId(3);
        u.setRole(role);
        return u;
    }

    private TypeSeance type() {
        TypeSeance t = new TypeSeance();
        t.setId(1);
        t.setDureeMinimale(45);
        t.setDureeMaximale(90);
        return t;
    }

    /** Séance valide : coach Coach, durée 60 (dans 45-90), demain 10h. */
    private Seance seanceValide() {
        Seance s = new Seance();
        s.setDate(LocalDate.now().plusDays(5));
        s.setHeureDebut(LocalTime.of(10, 0));
        s.setDureeMinutes(60);
        s.setStatut(StatutSeance.ACTIVE);
        s.setCoach(coach("Coach"));
        s.setTypeSeance(type());
        return s;
    }

    // ===================== validerCoach =====================

    @Test
    @DisplayName("insert : l'utilisateur assigné n'a pas le rôle Coach -> 400")
    void insert_coachPasRoleCoach_leve400() {
        Seance seance = seanceValide();
        seance.setCoach(coach("Adherent")); // mauvais rôle

        when(utilisateurDao.findById(3)).thenReturn(Optional.of(seance.getCoach()));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.insert(seance));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(seanceDao, never()).save(any());
    }

    @Test
    @DisplayName("insert : coach introuvable -> 400")
    void insert_coachIntrouvable_leve400() {
        Seance seance = seanceValide();
        when(utilisateurDao.findById(3)).thenReturn(Optional.empty());

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.insert(seance));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // ===================== validerDuree =====================

    @Test
    @DisplayName("insert : durée inférieure au minimum du type -> 400")
    void insert_dureeTropCourte_leve400() {
        Seance seance = seanceValide();
        seance.setDureeMinutes(30); // < 45

        when(utilisateurDao.findById(3)).thenReturn(Optional.of(seance.getCoach()));
        when(typeSeanceDao.findById(1)).thenReturn(Optional.of(type()));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.insert(seance));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(seanceDao, never()).save(any());
    }

    @Test
    @DisplayName("insert : durée supérieure au maximum du type -> 400")
    void insert_dureeTropLongue_leve400() {
        Seance seance = seanceValide();
        seance.setDureeMinutes(120); // > 90

        when(utilisateurDao.findById(3)).thenReturn(Optional.of(seance.getCoach()));
        when(typeSeanceDao.findById(1)).thenReturn(Optional.of(type()));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.insert(seance));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    // ===================== validerChevauchement =====================

    @Test
    @DisplayName("insert : chevauchement avec une autre séance du coach -> 409")
    void insert_chevauchement_leve409() {
        Seance seance = seanceValide(); // 10:00 -> 11:00

        Seance autre = seanceValide();
        autre.setId(99);
        autre.setHeureDebut(LocalTime.of(10, 30)); // 10:30 -> 11:30, chevauche

        when(utilisateurDao.findById(3)).thenReturn(Optional.of(seance.getCoach()));
        when(typeSeanceDao.findById(1)).thenReturn(Optional.of(type()));
        when(seanceDao.findByCoachAndDate(any(), any())).thenReturn(List.of(autre));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.insert(seance));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(seanceDao, never()).save(any());
    }

    @Test
    @DisplayName("insert : une séance ANNULEE qui chevauche est ignorée -> sauvegarde OK")
    void insert_chevauchementAvecAnnulee_estIgnore() {
        Seance seance = seanceValide();

        Seance annulee = seanceValide();
        annulee.setId(99);
        annulee.setHeureDebut(LocalTime.of(10, 30));
        annulee.setStatut(StatutSeance.ANNULEE); // annulée => ignorée

        when(utilisateurDao.findById(3)).thenReturn(Optional.of(seance.getCoach()));
        when(typeSeanceDao.findById(1)).thenReturn(Optional.of(type()));
        when(seanceDao.findByCoachAndDate(any(), any())).thenReturn(List.of(annulee));

        service.insert(seance); // ne doit PAS lever 409

        verify(seanceDao).save(seance);
    }

    @Test
    @DisplayName("insert : cas valide -> statut ACTIVE par défaut + sauvegarde")
    void insert_casValide_statutActifParDefaut() {
        Seance seance = seanceValide();
        seance.setStatut(null); // doit être défini à ACTIVE par le service

        when(utilisateurDao.findById(3)).thenReturn(Optional.of(seance.getCoach()));
        when(typeSeanceDao.findById(1)).thenReturn(Optional.of(type()));
        when(seanceDao.findByCoachAndDate(any(), any())).thenReturn(new ArrayList<>());

        service.insert(seance);

        Assertions.assertEquals(StatutSeance.ACTIVE, seance.getStatut());
        verify(seanceDao).save(seance);
    }

    // ===================== delete =====================

    @Test
    @DisplayName("delete : séance inexistante -> SeanceNotFoundException")
    void delete_inexistante_leveNotFound() {
        when(seanceDao.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(SeanceNotFoundException.class, () -> service.delete(99));
        verify(seanceDao, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete : séance avec des inscriptions -> 409")
    void delete_avecInscriptions_leve409() {
        Seance seance = seanceValide();
        seance.setInscriptions(List.of(new Inscription())); // au moins 1 inscription

        when(seanceDao.findById(1)).thenReturn(Optional.of(seance));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.delete(1));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(seanceDao, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete : séance sans inscription -> suppression OK")
    void delete_sansInscription_supprime() {
        Seance seance = seanceValide();
        seance.setInscriptions(new ArrayList<>());

        when(seanceDao.findById(1)).thenReturn(Optional.of(seance));

        service.delete(1);

        verify(seanceDao).deleteById(1);
    }
}
