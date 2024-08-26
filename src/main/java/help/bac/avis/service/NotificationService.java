package help.bac.avis.service;

import help.bac.avis.entite.Validation;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {
    JavaMailSender javaMailSender;

    // cette methode nous permet d'envoyer des mails aux utilisateurs après leurs inscriptions
    public void envoyer(Validation validation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("no-reply@help.bac");
        message.setTo(validation.getUtilisateur().getEmail());
        message.setSubject("Votre code d'activation");

        String text = String.format(
                "Bonjour %s, votre code d'activation est : %s",
                validation.getUtilisateur().getNom(), validation.getCode()
        );

        message.setText(text);
        javaMailSender.send(message);
    }
}
