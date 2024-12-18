package help.bac.avis.controleur;

import help.bac.avis.dto.UtilisateurDTO;
import help.bac.avis.entite.Avis;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.securite.JwtService;
import help.bac.avis.service.AvisService;
import help.bac.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/utilisateurs")
public class UtilisateurControleur {

    private final UtilisateurService utilisateurService;
    private JwtService jwtService;

    @PreAuthorize("hasAuthority('ADMINISTRATEUR_READ')") //Seul les administrateurs peuvent accéder à cette route
    @GetMapping
    public List<Utilisateur> liste() {
        return this.utilisateurService.liste();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/donnees")
    public Map<String, Object> donneesUtilisateur(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String email = this.jwtService.extractUsername(token);
        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(email);

        UtilisateurDTO utilisateurDTO = new UtilisateurDTO(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.isActif(),
                utilisateur.getRole().getLibelle().toString()
        );

        return Map.of("status", 200, "utilisateur", utilisateurDTO);
    };
}
