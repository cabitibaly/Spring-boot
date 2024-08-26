package help.bac.avis.service;

import help.bac.avis.entite.Avis;
import help.bac.avis.repository.AvisRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service // Service c'est une classe qui est déclarée comme service
public class AvisService {

    private final AvisRepository avisRepository; // Ceci nous permet d'injecter la classe AvisRepository pour les operations de base de données

    public void creer(Avis avis) { // Nous créons un nouvel avis
        this.avisRepository.save(avis);
    }
}
