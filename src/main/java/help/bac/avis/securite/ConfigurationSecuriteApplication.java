package help.bac.avis.securite;

import help.bac.avis.service.UtilisateurService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
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
                                                    .requestMatchers(HttpMethod.POST,"/connexion").permitAll()
                                                    .anyRequest().authenticated()
                            ).build();
    }

    @Bean // pour le cryptage des mots de passe
    public BCryptPasswordEncoder passwordEncoder() { // Il est fourni par Spring pour crypter les mots de passe
        return  new BCryptPasswordEncoder();
    }

    /*
    *  AuthenticationManager va s'occuper de gérer l'authentification des utilisateurs
    */

    @Bean // spring met à notre disposition une configuration pour authentication manager
    public AuthenticationManager autenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    /*
    * AuthenticationManager s'appuis sur un authencationProvider.
    * Ce dernier va s'occuper d'accéder à la base de données
    * */

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(); // accès à la base de données
        // Utilisation de la dependance injectée
        daoAuthenticationProvider.setUserDetailsService(userDetailsService); // on lui passe notre service utilisateur
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder()); // on lui passe notre cryptage
        return daoAuthenticationProvider;
    }
}
