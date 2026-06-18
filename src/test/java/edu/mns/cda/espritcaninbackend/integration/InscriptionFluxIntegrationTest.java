package edu.mns.cda.espritcaninbackend.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'INTÉGRATION du flux métier d'inscription / appel, de bout en bout
 * (route → sécurité → service → règles métier → H2). On vérifie les codes
 * HTTP renvoyés par les vraies règles : 201 / 409 / 403 / 404 / 204.
 *
 * {@code @Transactional} : chaque test s'exécute dans une transaction
 * annulée (rollback) à la fin → les écritures (POST/PATCH) ne polluent pas
 * les autres tests. Base de départ = data-init.sql.
 *
 * Lancé en IDE uniquement (package `integration` exclu du `mvn test`).
 *
 * Rappels du seed (V1) :
 *  - jean.dupont@mail.fr (Adhérent) possède Rex(1), Luna(2) ; Rex inscrit S1.
 *  - Bella(3) appartient à Sophie (id 2).
 *  - Coachs : Lucas(12), Camille(13), Thomas(14).
 *  - S1 (Obé déb, passée, coach Lucas) : Rex & Luna PRESENT.
 *  - S11/S13 (à venir, coach Thomas) ; Volt(11) inscrit S13.
 */
@SpringBootTest
@Transactional
@RequiredArgsConstructor
class InscriptionFluxIntegrationTest {

    private final WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // ===================================================================
    //  POST /inscription/mes-inscriptions (adhérent inscrit son chien)
    // ===================================================================

    @Test
    @WithUserDetails("jean.dupont@mail.fr")
    @DisplayName("Inscription : mon chien à une séance valide -> 201")
    void inscrire_casValide_retourne201() throws Exception {
        mvc.perform(post("/inscription/mes-inscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chienId\":1,\"seanceId\":11}"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("jean.dupont@mail.fr")
    @DisplayName("Inscription : doublon (Rex déjà inscrit séance 1) -> 409")
    void inscrire_doublon_retourne409() throws Exception {
        mvc.perform(post("/inscription/mes-inscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chienId\":1,\"seanceId\":1}"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithUserDetails("jean.dupont@mail.fr")
    @DisplayName("Inscription : chien d'un autre adhérent (Bella) -> 403")
    void inscrire_chienNonPossede_retourne403() throws Exception {
        mvc.perform(post("/inscription/mes-inscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chienId\":3,\"seanceId\":4}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("jean.dupont@mail.fr")
    @DisplayName("Inscription : chien introuvable -> 404")
    void inscrire_chienIntrouvable_retourne404() throws Exception {
        mvc.perform(post("/inscription/mes-inscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chienId\":9999,\"seanceId\":4}"))
                .andExpect(status().isNotFound());
    }

    // ===================================================================
    //  PATCH /inscription/{idChien}/{idSeance}/presence (appel du coach)
    // ===================================================================

    @Test
    @WithUserDetails("lucas.bernard@mail.fr")
    @DisplayName("Appel : coach marque Rex PRESENT sur séance 1 (terminée) -> 204")
    void faireAppel_casValide_retourne204() throws Exception {
        mvc.perform(patch("/inscription/1/1/presence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statutPresence\":\"PRESENT\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails("thomas.garcia@mail.fr")
    @DisplayName("Appel : séance 13 (à venir) pas encore terminée -> 409")
    void faireAppel_seanceNonTerminee_retourne409() throws Exception {
        mvc.perform(patch("/inscription/11/13/presence")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statutPresence\":\"PRESENT\"}"))
                .andExpect(status().isConflict());
    }
}
