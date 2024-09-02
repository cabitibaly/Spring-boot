package help.bac.avis.controleur;

import help.bac.avis.entite.Avis;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.service.AvisService;
import help.bac.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/utilisateurs")
public class UtilisateurControleur {

    private final UtilisateurService utilisateurService;

    @PreAuthorize("hasAuthority('ADMINISTRATEUR_READ')") //Seul les administrateurs peuvent accéder à cette route
    @GetMapping
    public List<Utilisateur> liste() {
        return this.utilisateurService.liste();
    }

}
