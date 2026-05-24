package edu.mns.cda.espritcaninbackend.service;

import edu.mns.cda.espritcaninbackend.dao.ChienDao;
import edu.mns.cda.espritcaninbackend.dao.SeanceDao;
import edu.mns.cda.espritcaninbackend.dao.UtilisateurDao;
import edu.mns.cda.espritcaninbackend.dto.DashboardDto;
import edu.mns.cda.espritcaninbackend.model.Seance;
import edu.mns.cda.espritcaninbackend.model.StatutSeance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int NB_PROCHAINES_SEANCES = 5;

    protected final UtilisateurDao utilisateurDao;
    protected final ChienDao chienDao;
    protected final SeanceDao seanceDao;

    public DashboardDto getDashboard(){
        LocalDate today = LocalDate.now();
        LocalDate debutMois = today.withDayOfMonth(1);
        LocalDate finMois = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate debutMoisDernier = debutMois.minusMonths(1);
        LocalDate finMoisDernier = debutMois.minusDays(1);

        // KPIs simples
        long totalUtilisateurs = utilisateurDao.count();
        long nouveauxUtilisateursCeMois = utilisateurDao.countNouveauxDepuis(debutMois);
        long totalChiens = chienDao.count();
        long seancesMois = seanceDao.countByStatutEntre(debutMois, finMois, StatutSeance.ACTIVE);
        long seancesMoisDernier = seanceDao.countByStatutEntre(debutMoisDernier, finMoisDernier, StatutSeance.ACTIVE);

        List<Seance> activesAVenir = seanceDao.findByStatutDepuis(today, StatutSeance.ACTIVE);

        int tauxRemplissageMoyen = calculerTauxRemplissageMoyen(activesAVenir);

        List<Seance> prochaines = activesAVenir.stream()
                .limit(NB_PROCHAINES_SEANCES)
                .toList();

        return new DashboardDto(
                totalUtilisateurs,
                nouveauxUtilisateursCeMois,
                totalChiens,
                seancesMois,
                seancesMoisDernier,
                tauxRemplissageMoyen,
                prochaines
        );
    }

    /**
     * Calcule le taux de remplissage moyen sur les séances à venir.
     * Pour chaque séance : (nb inscrits / capacité max) * 100
     * Retourne la moyenne arrondie à l'entier, ou 0 s'il n'y a aucune séance à venir.
     */
    private int calculerTauxRemplissageMoyen(List<Seance> seances) {
        if (seances.isEmpty()) {
            return 0;
        }

        double moyenne = seances.stream()
                .mapToDouble(s -> {
                    int inscrits = s.getInscriptions() == null ? 0 : s.getInscriptions().size();
                    int capacite = s.getTypeSeance().getParticipantsMaximum();
                    return capacite == 0 ? 0.0 : ((double) inscrits / capacite) * 100;
                })
                .average()
                .orElse(0.0);

        return (int) Math.round(moyenne);
    }
}
