package edu.mns.cda.espritcaninbackend.config;

import edu.mns.cda.espritcaninbackend.dao.RoleDao;
import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.model.Role;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Garantit qu'un compte Admin existe au démarrage (résilience : base fraîche ou
 * démo sans seeding SQL). Idempotent : ne crée rien si un admin est déjà présent.
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);
    private static final String ROLE_ADMIN = "Admin";

    private final RoleDao roleDao;
    private final UtilisateurDao utilisateurDao;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminInitializer(RoleDao roleDao, UtilisateurDao utilisateurDao, PasswordEncoder passwordEncoder,
                            @Value("${admin.email}") String adminEmail,
                            @Value("${admin.password}") String adminPassword) {
        this.roleDao = roleDao;
        this.utilisateurDao = utilisateurDao;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        // Rôle Admin = donnée de référence. On le crée s'il manque (base vide sans seeding SQL).
        Role roleAdmin = roleDao.findByNom(ROLE_ADMIN).orElseGet(() -> {
            Role role = new Role();
            role.setNom(ROLE_ADMIN);
            role.setDescription("Administrateur du club");
            log.info("Rôle '{}' absent : création.", ROLE_ADMIN);
            return roleDao.save(role);
        });

        // Un admin existe déjà ? on ne touche à rien.
        if (utilisateurDao.countByRoleNom(ROLE_ADMIN) > 0) {
            return;
        }

        // Aucun admin : on bootstrappe un compte depuis les variables d'env.
        Utilisateur admin = new Utilisateur();
        admin.setNom("Admin");
        admin.setPrenom("Esprit Canin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(roleAdmin);
        utilisateurDao.save(admin);
        log.warn("Aucun admin trouvé : compte admin créé pour '{}'.", adminEmail);
    }
}