package help.bac.avis.controleur;

import help.bac.avis.dto.AuthentificationDTO;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.securite.JwtService;
import help.bac.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilisateurControleur {

    private final UtilisateurService utilisateurService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/inscription")
    public void inscription(@RequestBody Utilisateur utilisateur) {
        this.utilisateurService.inscription(utilisateur);
        log.info("Inscription");
    }

    @PostMapping(path = "/activation")
    public void activation(@RequestBody Map<String, String> activation) {
        this.utilisateurService.activation(activation);
    }

    @PostMapping(path = "/deconnexion")
    public void deconnexion() {
        this.jwtService.deconnexion();
    }

    @PostMapping(path = "/connexion")
    public Map<String, String> connexion(@RequestBody AuthentificationDTO authentificationDTO) {

        // Authentification d'un utilisateur avec son email et mot de passe
        Authentication authenticate = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationDTO.username(), authentificationDTO.password())
        );

        if(authenticate.isAuthenticated()) {
            return this.jwtService.generate(authentificationDTO.username());
        }

        return  null;
    }
}
