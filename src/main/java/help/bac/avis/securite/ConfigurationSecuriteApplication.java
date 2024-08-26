package help.bac.avis.securite;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Permet de déclarer la configuration de sécurité
public class ConfigurationSecuriteApplication {

    @Bean // Un bean est une classe qu'on peut instancier
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception { // Configuration de sécurité
            return
                    httpSecurity
                            .csrf(AbstractHttpConfigurer::disable)
                            .authorizeHttpRequests(
                                    authorize ->
                                            authorize
                                                    .requestMatchers(HttpMethod.POST,"/inscription").permitAll()
                                                    .requestMatchers(HttpMethod.POST,"/activation").permitAll()
                                                    .anyRequest().authenticated()
                            ).build();
    }

    @Bean // pour le cryptage des mots de passe
    public BCryptPasswordEncoder passwordEncoder() { // Il est fourni par Spring pour crypter les mots de passe
        return  new BCryptPasswordEncoder();
    }
}
