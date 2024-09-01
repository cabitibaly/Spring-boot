package help.bac.avis.service;

import help.bac.avis.entite.Utilisateur;
import help.bac.avis.entite.Validation;
import help.bac.avis.repository.ValidationRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class ValidationService {
    // Injection de dépendances
    private final ValidationRepository validationRepository;
    private NotificationService notificationService; // Pour envoyer les notifications

    public void enregistrer(Utilisateur utilisateur) {
        Validation validation = new Validation(); // Création d'une validation
        validation.setUtilisateur(utilisateur);
        Instant creation = Instant.now();
        Instant expiration = creation.plus(10, ChronoUnit.MINUTES);

        Random randon = new Random();
        int randomInteger = randon.nextInt(999999);
        String code = String.format("%06d", randomInteger);

        validation.setCreation(creation);
        validation.setExpiration(expiration);
        validation.setCode(code);

        this.validationRepository.save(validation);
        this.notificationService.envoyer(validation);
    }

    public Validation lireEnFonctionDuCode(String code) {
        return this.validationRepository.findByCode(code).orElseThrow(() -> new RuntimeException("Code invalide"));
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void nettoyerTable() {
        this.validationRepository.deleteAllByExpirationBefore(Instant.now());
    }

}
