package help.bac.avis.controleur;


import help.bac.avis.entite.Avis;
import help.bac.avis.service.AvisService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/avis") // Cette annotation permet de définir une route pour les endpoints
@RestController
public class AvisControleur {
    private final AvisService avisService; // Ceci nous permet d'injecter la classe AvisService

    @ResponseStatus(HttpStatus.CREATED) // Cette annotation permet de définir le status HTTP de la réponse
    @PostMapping
    public void creerAvis(@RequestBody Avis avis) { // Nous créons un nouvel avis
        this.avisService.creer(avis);
    }
}
