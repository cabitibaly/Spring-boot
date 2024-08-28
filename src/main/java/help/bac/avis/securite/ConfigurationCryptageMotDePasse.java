package help.bac.avis.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class ConfigurationCryptageMotDePasse {
    @Bean // pour le cryptage des mots de passe
    public BCryptPasswordEncoder passwordEncoder() { // Il est fourni par Spring pour crypter les mots de passe
        return  new BCryptPasswordEncoder();
    }
}
