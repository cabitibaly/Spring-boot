package help.bac.avis.securite;

import help.bac.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity // Restriction sur des methods
@EnableWebSecurity // Permet de déclarer la configuration de sécurité
public class ConfigurationSecuriteApplication {

    private final UserDetailsService userDetailsService;
    private final JwtFilter jwtFilter;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ConfigurationSecuriteApplication(UserDetailsService userDetailsService, JwtFilter jwtFilter, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5176"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean // Un bean est une classe qu'on peut instancier
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception { // Configuration de sécurité
            return
                    httpSecurity
                            .cors(c -> c.configurationSource(corsConfigurationSource()))
                            .csrf(AbstractHttpConfigurer::disable)
                            .authorizeHttpRequests(
                                    authorize ->
                                            authorize
                                                    .requestMatchers(HttpMethod.POST,"/inscription").permitAll()
                                                    .requestMatchers(HttpMethod.POST,"/activation").permitAll()
                                                    .requestMatchers(HttpMethod.POST,"/connexion").permitAll()
                                                    .requestMatchers(HttpMethod.POST,"/refresh-token").permitAll()
                                                    .requestMatchers(HttpMethod.POST,"/modification-mot-de-passe").permitAll()
                                                    .requestMatchers(HttpMethod.POST,"/nouveau-mot-de-passe").permitAll()
                                                    .requestMatchers(HttpMethod.GET,"/pdf").permitAll()
                                                    .requestMatchers(HttpMethod.GET,"/telecharger").permitAll()
                                                    .requestMatchers("/ws").permitAll()
                                                    .requestMatchers("/ws/**", "/ws/socket", "/ws/success").permitAll()
                                                    .requestMatchers(HttpMethod.GET, "/utilisateurs/donnees").hasAnyAuthority("ROLE_MANAGER","ROLE_ADMINISTRATEUR", "ROLE_UTILISATEUR") // On autorise l'accès à l'endpoint /utilisateurs/donnees seulement pour les utilisateurs ayant le role ADMINISTRATEUR
                                                    .requestMatchers(HttpMethod.POST, "/tickets/creer-un-ticket").hasAnyAuthority("ROLE_MANAGER","ROLE_ADMINISTRATEUR", "ROLE_UTILISATEUR") // On autorise l'accès à l'endpoint /tickets/creer-un-ticket seulement pour les utilisateurs ayant le role ADMINISTRATEUR
                                                    .requestMatchers(HttpMethod.GET, "/tickets/tous-les-tickets").hasAnyAuthority("ROLE_MANAGER","ROLE_ADMINISTRATEUR", "ROLE_UTILISATEUR") // On autorise l'accès à l'endpoint /tickets/creer-un-ticket seulement pour les utilisateurs ayant le role ADMINISTRATEUR
                                                    .requestMatchers(HttpMethod.GET, "/tickets/tous-les-tickets/{id}").hasAnyAuthority("ROLE_MANAGER","ROLE_ADMINISTRATEUR", "ROLE_UTILISATEUR") // On autorise l'accès à l'endpoint /tickets/creer-un-ticket seulement pour les utilisateurs ayant le role ADMINISTRATEUR
                                                    .requestMatchers(HttpMethod.GET, "/tickets/tous-les-tickets-client/{id}").hasAnyAuthority("ROLE_MANAGER","ROLE_ADMINISTRATEUR", "ROLE_UTILISATEUR") // On autorise l'accès à l'endpoint /tickets/creer-un-ticket seulement pour les utilisateurs ayant le role ADMINISTRATEUR
                                                    .requestMatchers(HttpMethod.GET, "/tickets/tous-les-tickets-client").hasAnyAuthority("ROLE_MANAGER","ROLE_ADMINISTRATEUR", "ROLE_UTILISATEUR") // On autorise l'accès à l'endpoint /tickets/creer-un-ticket seulement pour les utilisateurs ayant le role ADMINISTRATEUR
                                                    .requestMatchers(HttpMethod.GET, "/avis").hasAnyAuthority("ROLE_MANAGER","ROLE_ADMINISTRATEUR") // On autorise l'accès à l'endpoint /avis seulement pour les utilisateurs ayant le role ADMINISTRATEUR
                                                    .requestMatchers(HttpMethod.GET, "/conversation").hasAnyAuthority("ROLE_MANAGER","ROLE_ADMINISTRATEUR", "ROLE_UTILISATEUR")
                                                    .anyRequest().authenticated()
                            ) // Une session pour l'authentification de l'utilisateur car spring fonctionne en session
                            .sessionManagement(httpSecuritySessionManagementConfigurer ->
                                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // On verifie le token chaque fois qu'une requête est faite

                                    )
                            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                            .build();
    }

    /*
    *  AuthenticationManager va s'occuper de gérer l'authentification des utilisateurs
    */

    @Bean // spring met à notre disposition une configuration pour authentication manager
    public AuthenticationManager autenticwationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /*
    * AuthenticationManager s'appuis sur un authencationProvider.
    * Ce dernier va s'occuper d'accéder à la base de données
    * */

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(); // accès à la base de données
        // Utilisation de la dependance injectée
        daoAuthenticationProvider.setUserDetailsService(userDetailsService); // on lui passe notre service utilisateur
        daoAuthenticationProvider.setPasswordEncoder(this.bCryptPasswordEncoder); // on lui passe notre cryptage
        return daoAuthenticationProvider;
    }
}
