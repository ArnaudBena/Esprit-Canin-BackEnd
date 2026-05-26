package edu.mns.cda.espritcaninbackend.security;

import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.model.Utilisateur;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Lien entre Spring Security et la BDD.
 * Spring appelle loadUserByUsername(email) au login pour récupérer
 * le UserDetails associé. Si l'email est inconnu : UsernameNotFoundException
 */
@Service
@RequiredArgsConstructor
public class UtilisateurDetailsService implements UserDetailsService {

    protected final UtilisateurDao utilisateurDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email inconnu : " + email));

        return new UtilisateurDetails(utilisateur);
    }
}
