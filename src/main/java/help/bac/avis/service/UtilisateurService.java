package help.bac.avis.service;

import help.bac.avis.TypeDeRole;
import help.bac.avis.entite.Role;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.entite.Validation;
import help.bac.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UtilisateurService {
    // Injection de dépendances
    private final UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;

    public void inscription(Utilisateur utilisateur) {
        if(!utilisateur.getEmail().contains("@")) {
            throw new RuntimeException("Email invalide");
        }

        Optional<Utilisateur> utilisateurOptional = this.utilisateurRepository.findByEmail(utilisateur.getEmail());

        if(utilisateurOptional.isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // On crypte le mot de passe
        String mdpCrypte = passwordEncoder.encode(utilisateur.getMdp());
        utilisateur.setMdp(mdpCrypte);

        // On attribue un rôle utilisateur
        Role roleUtilisateur = new Role();
        roleUtilisateur.setLibelle(TypeDeRole.UTILISATEUR);
        utilisateur.setRole(roleUtilisateur);

        utilisateur = this.utilisateurRepository.save(utilisateur);
        this.validationService.enregistrer(utilisateur);
    }

    public void activation(Map<String, String> activation) {
        Validation validation = this.validationService.lireEnFonctionDuCode(activation.get("code"));

        if(Instant.now().isAfter(validation.getExpiration())) {
            throw new RuntimeException("Code expiré");
        }

        Utilisateur utilisateurActiver = this.utilisateurRepository.findById(validation.getUtilisateur().getId()).orElseThrow(
                () -> new RuntimeException("Utilisateur inconnu")
        );

        utilisateurActiver.setActif(true);
        this.utilisateurRepository.save(utilisateurActiver);
    }
}
