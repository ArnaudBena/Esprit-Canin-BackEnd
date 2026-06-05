package edu.mns.cda.espritcaninbackend.unit.service;

import edu.mns.cda.espritcaninbackend.dao.ChienCompetenceDao;
import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.dao.InscriptionDao;
import edu.mns.cda.espritcaninbackend.dao.SeanceDao;
import edu.mns.cda.espritcaninbackend.exception.*;
import edu.mns.cda.espritcaninbackend.model.*;
import edu.mns.cda.espritcaninbackend.service.InscriptionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
 * Tests UNITAIRES du moteur métier d'inscription.
 *
 * On teste {@link InscriptionService} en ISOLATION : les 4 DAO sont des mocks
 * Mockito (aucune base de données, aucun contexte Spring). On contrôle ce que
 * renvoient les DAO et on vérifie le comportement du service (exceptions levées,
 * objet sauvegardé, valeurs calculées).
 *
 * Pattern : @Mock crée les faux DAO, @InjectMocks construit le vrai service en
 * lui injectant ces mocks via son constructeur (@RequiredArgsConstructor).
 */
@ExtendWith(MockitoExtension.class)
class InscriptionServiceUnitTest {

    @Mock
    private InscriptionDao inscriptionDao;
    @Mock
    private ChienDao chienDao;
    @Mock
    private SeanceDao seanceDao;
    @Mock
    private ChienCompetenceDao chienCompetenceDao;

    @InjectMocks
    private InscriptionService service;

    // ===================================================================
    //  FIXTURES : objets de test construits à la main (Lombok setters)
    // ===================================================================

    /** Type de séance "ouvert" : large fourchette d'âge, sans prérequis. */
    private TypeSeance typeOuvert() {
        TypeSeance type = new TypeSeance();
        type.setId(1);
        type.setLibelle("Obéissance débutant");
        type.setAgeMinimumMois(0);
        type.setAgeMaximumMois(360);
        type.setParticipantsMaximum(6);
        type.setTypeSeancesCompetences(null);          // aucun prérequis
        type.setTypeSeancesCompetencesConferees(null); // ne confère rien
        return type;
    }

    /** Chien adulte (3 ans), sans aucune compétence enregistrée. */
    private Chien chienValide() {
        Chien chien = new Chien();
        chien.setId(1);
        chien.setNom("Rex");
        chien.setDateNaissance(LocalDate.now().minusYears(3));
        chien.setChienCompetences(null); // pas de ligne => DEBUTANT par défaut
        return chien;
    }

    /** Séance ACTIVE, à venir, avec un type ouvert et aucun inscrit. */
    private Seance seanceAVenir(TypeSeance type) {
        Seance seance = new Seance();
        seance.setId(1);
        seance.setStatut(StatutSeance.ACTIVE);
        seance.setDate(LocalDate.now().plusDays(10));
        seance.setHeureDebut(LocalTime.of(10, 0));
        seance.setDureeMinutes(60);
        seance.setTypeSeance(type);
        seance.setInscriptions(new ArrayList<>());
        return seance;
    }

    /** Séance déjà terminée (hier), utile pour appel/validation. */
    private Seance seanceTerminee(TypeSeance type) {
        Seance seance = seanceAVenir(type);
        seance.setDate(LocalDate.now().minusDays(1)); // hier => getTerminee() = true
        return seance;
    }

    // ===================================================================
    //  inscrire() — cas nominal
    // ===================================================================

    @Test
    @DisplayName("inscrire : cas valide -> sauvegarde une inscription au statut INSCRIT")
    void inscrire_casValide_sauvegardeAvecStatutInscrit() {
        Chien chien = chienValide();
        Seance seance = seanceAVenir(typeOuvert());

        when(chienDao.findById(1)).thenReturn(Optional.of(chien));
        when(seanceDao.findById(1)).thenReturn(Optional.of(seance));
        when(inscriptionDao.findById(any())).thenReturn(Optional.empty()); // pas de doublon
        when(inscriptionDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.inscrire(1, 1);

        // On capture l'inscription réellement passée à save() pour l'inspecter
        ArgumentCaptor<Inscription> captor = ArgumentCaptor.forClass(Inscription.class);
        verify(inscriptionDao).save(captor.capture());
        Inscription enregistree = captor.getValue();

        Assertions.assertEquals(StatutPresence.INSCRIT, enregistree.getStatutPresence());
        Assertions.assertEquals(chien, enregistree.getChien());
        Assertions.assertEquals(seance, enregistree.getSeance());
    }

    // ===================================================================
    //  inscrire() — chargement des entités
    // ===================================================================

    @Test
    @DisplayName("inscrire : chien introuvable -> ChienNotFoundException")
    void inscrire_chienIntrouvable_leveChienNotFound() {
        when(chienDao.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ChienNotFoundException.class, () -> service.inscrire(99, 1));
        verify(inscriptionDao, never()).save(any());
    }

    @Test
    @DisplayName("inscrire : séance introuvable -> SeanceNotFoundException")
    void inscrire_seanceIntrouvable_leveSeanceNotFound() {
        when(chienDao.findById(1)).thenReturn(Optional.of(chienValide()));
        when(seanceDao.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(SeanceNotFoundException.class, () -> service.inscrire(1, 99));
        verify(inscriptionDao, never()).save(any());
    }

    // ===================================================================
    //  inscrire() — RG : les 6 règles métier
    // ===================================================================

    @Test
    @DisplayName("inscrire RG1 : séance annulée -> SeanceAnnuleeException")
    void inscrire_seanceAnnulee_leveException() {
        Seance seance = seanceAVenir(typeOuvert());
        seance.setStatut(StatutSeance.ANNULEE);

        when(chienDao.findById(1)).thenReturn(Optional.of(chienValide()));
        when(seanceDao.findById(1)).thenReturn(Optional.of(seance));

        Assertions.assertThrows(SeanceAnnuleeException.class, () -> service.inscrire(1, 1));
        verify(inscriptionDao, never()).save(any());
    }

    @Test
    @DisplayName("inscrire RG2 : séance déjà passée -> SeancePasseeException")
    void inscrire_seancePassee_leveException() {
        Seance seance = seanceAVenir(typeOuvert());
        seance.setDate(LocalDate.now().minusDays(1));

        when(chienDao.findById(1)).thenReturn(Optional.of(chienValide()));
        when(seanceDao.findById(1)).thenReturn(Optional.of(seance));

        Assertions.assertThrows(SeancePasseeException.class, () -> service.inscrire(1, 1));
        verify(inscriptionDao, never()).save(any());
    }

    @Test
    @DisplayName("inscrire RG3 : inscription en doublon -> InscriptionDoublonException")
    void inscrire_doublon_leveException() {
        when(chienDao.findById(1)).thenReturn(Optional.of(chienValide()));
        when(seanceDao.findById(1)).thenReturn(Optional.of(seanceAVenir(typeOuvert())));
        when(inscriptionDao.findById(any())).thenReturn(Optional.of(new Inscription())); // existe déjà

        Assertions.assertThrows(InscriptionDoublonException.class, () -> service.inscrire(1, 1));
        verify(inscriptionDao, never()).save(any());
    }

    @Test
    @DisplayName("inscrire RG4 : chien trop jeune pour le type -> EligibiliteAgeException")
    void inscrire_ageTropJeune_leveException() {
        TypeSeance type = typeOuvert();
        type.setAgeMinimumMois(12); // exige 12 mois minimum

        Chien chiot = chienValide();
        chiot.setDateNaissance(LocalDate.now().minusMonths(2)); // 2 mois seulement

        when(chienDao.findById(1)).thenReturn(Optional.of(chiot));
        when(seanceDao.findById(1)).thenReturn(Optional.of(seanceAVenir(type)));
        when(inscriptionDao.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(EligibiliteAgeException.class, () -> service.inscrire(1, 1));
        verify(inscriptionDao, never()).save(any());
    }

    @Test
    @DisplayName("inscrire RG5 : séance complète -> SeanceCompleteException")
    void inscrire_seanceComplete_leveException() {
        TypeSeance type = typeOuvert();
        type.setParticipantsMaximum(2);

        Seance seance = seanceAVenir(type);
        // 2 inscriptions actives => capacité atteinte
        Inscription i1 = new Inscription();
        i1.setStatutPresence(StatutPresence.INSCRIT);
        Inscription i2 = new Inscription();
        i2.setStatutPresence(StatutPresence.PRESENT);
        seance.setInscriptions(List.of(i1, i2));

        when(chienDao.findById(1)).thenReturn(Optional.of(chienValide()));
        when(seanceDao.findById(1)).thenReturn(Optional.of(seance));
        when(inscriptionDao.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(SeanceCompleteException.class, () -> service.inscrire(1, 1));
        verify(inscriptionDao, never()).save(any());
    }

    @Test
    @DisplayName("inscrire RG5 bis : les inscriptions ANNULEE ne comptent pas dans la capacité")
    void inscrire_annuleesNeComptentPas_inscriptionOk() {
        TypeSeance type = typeOuvert();
        type.setParticipantsMaximum(1);

        Seance seance = seanceAVenir(type);
        Inscription annulee = new Inscription();
        annulee.setStatutPresence(StatutPresence.ANNULEE); // ne compte pas
        seance.setInscriptions(new ArrayList<>(List.of(annulee)));

        when(chienDao.findById(1)).thenReturn(Optional.of(chienValide()));
        when(seanceDao.findById(1)).thenReturn(Optional.of(seance));
        when(inscriptionDao.findById(any())).thenReturn(Optional.empty());
        when(inscriptionDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.inscrire(1, 1); // ne doit PAS lever SeanceCompleteException

        verify(inscriptionDao).save(any());
    }

    @Test
    @DisplayName("inscrire RG6 : compétence requise non atteinte -> EligibiliteCompetenceException")
    void inscrire_competenceRequiseInsuffisante_leveException() {
        Competence agilite = new Competence();
        agilite.setId(2);
        agilite.setNom("Agilité");

        // Le type exige Agilité niveau INTERMEDIAIRE en prérequis
        TypeSeanceCompetence prerequis = new TypeSeanceCompetence();
        prerequis.setCompetence(agilite);
        prerequis.setNiveauMinimumRequis(NiveauCompetence.INTERMEDIAIRE);

        TypeSeance type = typeOuvert();
        type.setTypeSeancesCompetences(List.of(prerequis));

        // Chien sans compétence => DEBUTANT < INTERMEDIAIRE
        when(chienDao.findById(1)).thenReturn(Optional.of(chienValide()));
        when(seanceDao.findById(1)).thenReturn(Optional.of(seanceAVenir(type)));
        when(inscriptionDao.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(EligibiliteCompetenceException.class, () -> service.inscrire(1, 1));
        verify(inscriptionDao, never()).save(any());
    }

    // ===================================================================
    //  validerAcquisition() + appliquerProgression()
    // ===================================================================

    /** Inscription d'un chien PRESENT à une séance terminée dont le type confère une compétence. */
    private Inscription inscriptionPresentePourValidation(NiveauCompetence niveauConfere, Competence competence) {
        TypeSeanceCompetenceConferee conferee = new TypeSeanceCompetenceConferee();
        conferee.setCompetence(competence);
        conferee.setNiveauConfere(niveauConfere);

        TypeSeance type = typeOuvert();
        type.setTypeSeancesCompetencesConferees(List.of(conferee));

        Seance seance = seanceTerminee(type);

        Inscription inscription = new Inscription();
        inscription.setChien(chienValide());
        inscription.setSeance(seance);
        inscription.setStatutPresence(StatutPresence.PRESENT);
        inscription.setAcquisitionValidee(false);
        return inscription;
    }

    @Test
    @DisplayName("validerAcquisition : séance non terminée -> 409 CONFLICT")
    void validerAcquisition_seanceNonTerminee_leve409() {
        Inscription inscription = inscriptionPresentePourValidation(NiveauCompetence.DEBUTANT, new Competence());
        inscription.getSeance().setDate(LocalDate.now().plusDays(5)); // pas terminée

        when(inscriptionDao.findById(any())).thenReturn(Optional.of(inscription));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.validerAcquisition(new Inscription.Key(1, 1)));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    @DisplayName("validerAcquisition : chien non présent -> 409 CONFLICT")
    void validerAcquisition_chienNonPresent_leve409() {
        Inscription inscription = inscriptionPresentePourValidation(NiveauCompetence.DEBUTANT, new Competence());
        inscription.setStatutPresence(StatutPresence.INSCRIT); // appel pas (ou mal) fait

        when(inscriptionDao.findById(any())).thenReturn(Optional.of(inscription));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.validerAcquisition(new Inscription.Key(1, 1)));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    @DisplayName("validerAcquisition RG5/6 : chien DEBUTANT, type confère INTERMEDIAIRE -> progression + date")
    void validerAcquisition_progression_monteLeNiveau() {
        Competence agilite = new Competence();
        agilite.setId(2);
        agilite.setNom("Agilité");

        Inscription inscription = inscriptionPresentePourValidation(NiveauCompetence.INTERMEDIAIRE, agilite);

        when(inscriptionDao.findById(any())).thenReturn(Optional.of(inscription));
        when(chienCompetenceDao.findById(any())).thenReturn(Optional.empty()); // chien n'a pas encore la compétence
        when(chienCompetenceDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(inscriptionDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.validerAcquisition(new Inscription.Key(1, 1));

        ArgumentCaptor<ChienCompetence> captor = ArgumentCaptor.forClass(ChienCompetence.class);
        verify(chienCompetenceDao).save(captor.capture());
        ChienCompetence cc = captor.getValue();

        Assertions.assertEquals(NiveauCompetence.INTERMEDIAIRE, cc.getNiveauActuel());
        Assertions.assertEquals(inscription.getSeance().getDate(), cc.getDateAcquisition());
        Assertions.assertTrue(inscription.getAcquisitionValidee());
    }

    @Test
    @DisplayName("validerAcquisition RG7 : pas de rétrogradation (chien CONFIRME, type confère INTERMEDIAIRE)")
    void validerAcquisition_nonRegression_pasDeSave() {
        Competence agilite = new Competence();
        agilite.setId(2);

        Inscription inscription = inscriptionPresentePourValidation(NiveauCompetence.INTERMEDIAIRE, agilite);

        // Le chien possède déjà CONFIRME sur cette compétence
        ChienCompetence dejaConfirme = new ChienCompetence();
        dejaConfirme.setNiveauActuel(NiveauCompetence.CONFIRME);

        when(inscriptionDao.findById(any())).thenReturn(Optional.of(inscription));
        when(chienCompetenceDao.findById(any())).thenReturn(Optional.of(dejaConfirme));
        when(inscriptionDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.validerAcquisition(new Inscription.Key(1, 1));

        // Le niveau ne doit PAS être réécrit (INTERMEDIAIRE <= CONFIRME)
        verify(chienCompetenceDao, never()).save(any());
        Assertions.assertTrue(inscription.getAcquisitionValidee());
    }

    // ===================================================================
    //  faireAppel()
    // ===================================================================

    @Test
    @DisplayName("faireAppel : statut INSCRIT en paramètre -> 400 BAD_REQUEST")
    void faireAppel_statutInvalide_leve400() {
        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.faireAppel(new Inscription.Key(1, 1), StatutPresence.INSCRIT));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        verify(inscriptionDao, never()).save(any());
    }

    @Test
    @DisplayName("faireAppel : inscription annulée -> 409 CONFLICT")
    void faireAppel_inscriptionAnnulee_leve409() {
        Inscription inscription = new Inscription();
        inscription.setStatutPresence(StatutPresence.ANNULEE);
        inscription.setSeance(seanceTerminee(typeOuvert()));

        when(inscriptionDao.findById(any())).thenReturn(Optional.of(inscription));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.faireAppel(new Inscription.Key(1, 1), StatutPresence.PRESENT));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    @DisplayName("faireAppel : séance non terminée -> 409 CONFLICT")
    void faireAppel_seanceNonTerminee_leve409() {
        Inscription inscription = new Inscription();
        inscription.setStatutPresence(StatutPresence.INSCRIT);
        inscription.setSeance(seanceAVenir(typeOuvert())); // future => pas terminée

        when(inscriptionDao.findById(any())).thenReturn(Optional.of(inscription));

        ResponseStatusException ex = Assertions.assertThrows(ResponseStatusException.class,
                () -> service.faireAppel(new Inscription.Key(1, 1), StatutPresence.PRESENT));
        Assertions.assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    @DisplayName("faireAppel : cas valide -> enregistre le statut PRESENT")
    void faireAppel_casValide_enregistrePresent() {
        Inscription inscription = new Inscription();
        inscription.setStatutPresence(StatutPresence.INSCRIT);
        inscription.setSeance(seanceTerminee(typeOuvert()));

        when(inscriptionDao.findById(any())).thenReturn(Optional.of(inscription));
        when(inscriptionDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        service.faireAppel(new Inscription.Key(1, 1), StatutPresence.PRESENT);

        Assertions.assertEquals(StatutPresence.PRESENT, inscription.getStatutPresence());
        verify(inscriptionDao).save(inscription);
    }
}
