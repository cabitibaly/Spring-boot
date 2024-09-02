package help.bac.avis.service;

import help.bac.avis.enums.TypeDeRole;
import help.bac.avis.entite.Role;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.entite.Validation;
import help.bac.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UtilisateurService implements UserDetailsService { // UserDetailsService est une interface qui permet de gérer les utilisateurs

    // Injection de dépendances
    private final UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private final ValidationService validationService;


    @Override // LoadUserByUsername permet de récupérer les informations d'un utilisateur
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository
                .findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu"));
    }

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

    public void modificationMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur = (Utilisateur) this.loadUserByUsername(parametres.get("email"));
        this.validationService.enregistrer(utilisateur); // Probleme avec la clé étrangère
    }

    public void nouveauMotDePasse(Map<String, String> parametres) {
        Utilisateur utilisateur = (Utilisateur) this.loadUserByUsername(parametres.get("email"));
        Validation validation = this.validationService.lireEnFonctionDuCode(parametres.get("code"));

        if(validation.getUtilisateur().getEmail().equals(utilisateur.getEmail())) {
            String mdpCrypte = passwordEncoder.encode(parametres.get("password"));
            utilisateur.setMdp(mdpCrypte);
            this.utilisateurRepository.save(utilisateur);
        }

    }

    public List<Utilisateur> liste() {
        return (List<Utilisateur>) this.utilisateurRepository.findAll();
    }
}
