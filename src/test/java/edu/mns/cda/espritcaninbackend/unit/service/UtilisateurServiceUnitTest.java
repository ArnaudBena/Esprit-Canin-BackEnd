package edu.mns.cda.espritcaninbackend.unit.service;

import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.dao.InscriptionDao;
import edu.mns.cda.espritcaninbackend.dao.RoleDao;
import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.model.Chien;
import edu.mns.cda.espritcaninbackend.model.Role;
import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.model.StatutPresence;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import edu.mns.cda.espritcaninbackend.service.UtilisateurService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests UNITAIRES de {@link UtilisateurService} : gardes sur le rôle Admin
 * (non attribuable via l'API) et les deux suppressions (self RGPD + admin).
 * Tous les DAO sont mockés (aucune base, aucun contexte Spring).
 */
@ExtendWith(MockitoExtension.class)
class UtilisateurServiceUnitTest {

    @Mock
    private UtilisateurDao utilisateurDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleDao roleDao;
    @Mock
    private ChienDao chienDao;
    @Mock
    private InscriptionDao inscriptionDao;

    @InjectMocks
    private UtilisateurService service;

    // ===================== Fixtures =====================

    private Role role(int id, String nom) {
        Role r = new Role();
        r.setId(id);
        r.setNom(nom);
        return r;
    }

    private Utilisateur utilisateur(int id, String roleNom, int roleId) {
        Utilisateur u = new Utilisateur();
        u.setId(id);
        u.setRole(role(roleId, roleNom));
        return u;
    }

    // ===================== insert : garde rôle Admin =====================

    @Test
    @DisplayName("insert : rôle Admin demandé -> 403")
    void insert_roleAdmin_leve403() {
        Utilisateur u = utilisateur(0, "Admin", 3);
        when(roleDao.findById(3)).thenReturn(Optional.of(role(3, "Admin")));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.insert(u));
        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(utilisateurDao, never()).save(any());
    }

    @Test
    @DisplayName("insert : rôle Coach -> sauvegarde (password hashé)")
    void insert_roleCoach_sauvegarde() {
        Utilisateur u = utilisateur(0, "Coach", 2);
        u.setPassword("motdepasse123");
        when(roleDao.findById(2)).thenReturn(Optional.of(role(2, "Coach")));
        when(passwordEncoder.encode(any())).thenReturn("hash");

        service.insert(u);

        Assertions.assertEquals("hash", u.getPassword());
        verify(utilisateurDao).save(u);
    }

    // ===================== update : gardes Admin =====================

    @Test
    @DisplayName("update : la cible est un Admin -> 403")
    void update_cibleAdmin_leve403() {
        Utilisateur existant = utilisateur(4, "Admin", 3);
        when(utilisateurDao.findById(4)).thenReturn(Optional.of(existant));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.update(4, utilisateur(4, "Adherent", 1)));
        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(utilisateurDao, never()).save(any());
    }

    @Test
    @DisplayName("update : nouveau rôle Admin demandé -> 403")
    void update_nouveauRoleAdmin_leve403() {
        Utilisateur existant = utilisateur(1, "Adherent", 1);
        when(utilisateurDao.findById(1)).thenReturn(Optional.of(existant));
        when(roleDao.findById(3)).thenReturn(Optional.of(role(3, "Admin")));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.update(1, utilisateur(1, "Admin", 3)));
        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(utilisateurDao, never()).save(any());
    }

    @Test
    @DisplayName("update : adhérent -> Coach (autorisé) -> sauvegarde")
    void update_adherentVersCoach_sauvegarde() {
        Utilisateur existant = utilisateur(1, "Adherent", 1);
        existant.setNom("Ancien");

        Utilisateur modif = utilisateur(1, "Coach", 2);
        modif.setNom("Dupont");
        modif.setPrenom("Jean");
        modif.setEmail("jean@mail.fr");

        when(utilisateurDao.findById(1)).thenReturn(Optional.of(existant));
        when(roleDao.findById(2)).thenReturn(Optional.of(role(2, "Coach")));

        service.update(1, modif);

        Assertions.assertEquals("Dupont", existant.getNom());
        verify(utilisateurDao).save(existant);
    }

    // ===================== delete (admin) =====================

    @Test
    @DisplayName("delete : la cible est un Admin -> 403")
    void delete_cibleAdmin_leve403() {
        when(utilisateurDao.findById(4)).thenReturn(Optional.of(utilisateur(4, "Admin", 3)));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.delete(4));
        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(utilisateurDao, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete : coach avec des séances -> 409")
    void delete_coachAvecSeances_leve409() {
        Utilisateur coach = utilisateur(3, "Coach", 2);
        coach.setSeancesCoachees(List.of(new Seance()));
        when(utilisateurDao.findById(3)).thenReturn(Optional.of(coach));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.delete(3));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(utilisateurDao, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete : adhérent avec inscriptions futures -> 409")
    void delete_inscriptionsFutures_leve409() {
        Utilisateur adherent = utilisateur(1, "Adherent", 1);
        when(utilisateurDao.findById(1)).thenReturn(Optional.of(adherent));
        when(inscriptionDao.countInscriptionsFuturesActives(anyInt(), any(), any())).thenReturn(2L);

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.delete(1));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(utilisateurDao, never()).deleteById(any());
    }

    @Test
    @DisplayName("delete : adhérent sans engagement futur -> cascade + suppression")
    void delete_casValide_cascade() {
        Utilisateur adherent = utilisateur(1, "Adherent", 1);
        List<Chien> chiens = List.of(new Chien());
        adherent.setChiens(chiens);

        when(utilisateurDao.findById(1)).thenReturn(Optional.of(adherent));
        when(inscriptionDao.countInscriptionsFuturesActives(anyInt(), any(), any())).thenReturn(0L);

        service.delete(1);

        verify(chienDao).deleteAll(chiens);
        verify(utilisateurDao).deleteById(1);
    }

    // ===================== supprimerMonCompte (self RGPD) =====================

    @Test
    @DisplayName("supprimerMonCompte : un Admin ne peut pas se supprimer -> 403")
    void supprimerMonCompte_admin_leve403() {
        when(utilisateurDao.findById(4)).thenReturn(Optional.of(utilisateur(4, "Admin", 3)));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.supprimerMonCompte(4));
        Assertions.assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(utilisateurDao, never()).deleteById(any());
    }

    @Test
    @DisplayName("supprimerMonCompte : coach avec des séances -> 409")
    void supprimerMonCompte_coachAvecSeances_leve409() {
        Utilisateur coach = utilisateur(3, "Coach", 2);
        coach.setSeancesCoachees(List.of(new Seance()));
        when(utilisateurDao.findById(3)).thenReturn(Optional.of(coach));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.supprimerMonCompte(3));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(utilisateurDao, never()).deleteById(any());
    }

    @Test
    @DisplayName("supprimerMonCompte : adhérent -> cascade totale + suppression")
    void supprimerMonCompte_casValide_cascade() {
        Utilisateur adherent = utilisateur(1, "Adherent", 1);
        List<Chien> chiens = List.of(new Chien());
        adherent.setChiens(chiens);
        when(utilisateurDao.findById(1)).thenReturn(Optional.of(adherent));

        service.supprimerMonCompte(1);

        verify(chienDao).deleteAll(chiens);
        verify(utilisateurDao).deleteById(1);
        // pas de contrôle des inscriptions futures pour le self-delete (effacement total)
        verify(inscriptionDao, never()).countInscriptionsFuturesActives(anyInt(), any(), any());
    }
}
