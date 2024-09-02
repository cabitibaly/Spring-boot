package help.bac.avis.service;

import help.bac.avis.entite.Avis;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.repository.AvisRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service // Service c'est une classe qui est déclarée comme service
public class AvisService {

    private final AvisRepository avisRepository; // Ceci nous permet d'injecter la classe AvisRepository pour les operations de base de données

    public void creer(Avis avis) { // Nous créons un nouvel avis
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal(); // Nous récupérons l'utilisateur connecté
        avis.setUtilisateur(utilisateur); // Nous définissons l'utilisateur associé à l'avis
        this.avisRepository.save(avis);
    }

    public List<Avis> liste() {
        return (List<Avis>) this.avisRepository.findAll();
    }
}
