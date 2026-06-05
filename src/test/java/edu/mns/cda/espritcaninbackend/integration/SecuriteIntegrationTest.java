package edu.mns.cda.espritcaninbackend.integration;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests d'INTÉGRATION de la sécurité (cloisonnement des rôles).
 *
 * On charge tout le contexte Spring ({@code @SpringBootTest}) sur une base
 * H2 jetable seedée par {@code data-init.sql}, et on tape les vrais endpoints
 * HTTP via {@link MockMvc}. On vérifie les CODES de réponse (200 / 403).
 *
 * Deux façons de simuler un utilisateur connecté :
 *  - {@code @WithMockUser(roles=...)} : faux user avec un rôle, suffisant quand
 *    l'endpoint ne fait que vérifier le rôle (le corps ne lit pas le principal).
 *  - {@code @WithUserDetails("email")} : VRAI user chargé depuis la base via
 *    notre UserDetailsService — obligatoire quand l'endpoint lit
 *    {@code @AuthenticationPrincipal} (mes-seances, participants, mes-inscriptions).
 *
 * ⚠️ Lancé en IDE uniquement (package `integration` exclu du `mvn test`).
 *
 * Jeu de données (data-init.sql) :
 *  - jean.dupont@mail.fr  : Adhérent (possède Rex, Luna, inscrits séance 1)
 *  - lucas.bernard@mail.fr: Coach, assigné aux 4 séances
 *  - camille.petit@mail.fr: Coach, assigné à AUCUNE séance
 *  - arnaud.b@mail.fr     : Admin
 */
@SpringBootTest
@RequiredArgsConstructor
class SecuriteIntegrationTest {

    private final WebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        // apply(springSecurity()) branche toute la chaîne de filtres de sécurité
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    // ===================================================================
    //  Cloisonnement par rôle (vérification @PreAuthorize)
    // ===================================================================

    @Test
    @DisplayName("Catalogue : anonyme -> 403")
    void catalogue_anonyme_estRefuse() throws Exception {
        mvc.perform(get("/seance/catalogue"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADHERENT")
    @DisplayName("Catalogue : adhérent -> 200")
    void catalogue_adherent_estAutorise() throws Exception {
        mvc.perform(get("/seance/catalogue"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "COACH")
    @DisplayName("Catalogue : coach -> 403 (réservé adhérent)")
    void catalogue_coach_estRefuse() throws Exception {
        mvc.perform(get("/seance/catalogue"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADHERENT")
    @DisplayName("Mes séances (coach) : adhérent -> 403")
    void mesSeances_adherent_estRefuse() throws Exception {
        mvc.perform(get("/seance/mes-seances"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Liste séances (admin) : admin -> 200")
    void listeSeances_admin_estAutorise() throws Exception {
        mvc.perform(get("/seance/list"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADHERENT")
    @DisplayName("Liste séances (admin) : adhérent -> 403")
    void listeSeances_adherent_estRefuse() throws Exception {
        mvc.perform(get("/seance/list"))
                .andExpect(status().isForbidden());
    }

    // ===================================================================
    //  Endpoints à @AuthenticationPrincipal (vrai user requis)
    // ===================================================================

    @Test
    @WithUserDetails("lucas.bernard@mail.fr")
    @DisplayName("Mes séances : le coach voit ses séances -> 200")
    void mesSeances_coach_estAutorise() throws Exception {
        mvc.perform(get("/seance/mes-seances"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("jean.dupont@mail.fr")
    @DisplayName("Mes inscriptions : l'adhérent voit ses inscriptions -> 200")
    void mesInscriptions_adherent_estAutorise() throws Exception {
        mvc.perform(get("/inscription/mes-inscriptions"))
                .andExpect(status().isOk());
    }

    // ===================================================================
    //  Ownership : /seance/{id}/participants
    // ===================================================================

    @Test
    @WithUserDetails("lucas.bernard@mail.fr")
    @DisplayName("Participants séance 1 : coach PROPRIÉTAIRE -> 200")
    void participants_coachProprietaire_estAutorise() throws Exception {
        mvc.perform(get("/seance/1/participants"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("camille.petit@mail.fr")
    @DisplayName("Participants séance 1 : coach NON propriétaire -> 403")
    void participants_coachNonProprietaire_estRefuse() throws Exception {
        mvc.perform(get("/seance/1/participants"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("arnaud.b@mail.fr")
    @DisplayName("Participants séance 1 : admin (voit tout) -> 200")
    void participants_admin_estAutorise() throws Exception {
        mvc.perform(get("/seance/1/participants"))
                .andExpect(status().isOk());
    }
}
