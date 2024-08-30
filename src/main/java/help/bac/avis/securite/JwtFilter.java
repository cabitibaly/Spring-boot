package help.bac.avis.securite;

import help.bac.avis.service.UtilisateurService;
import help.bac.avis.entite.Jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Service
public class JwtFilter extends OncePerRequestFilter { // un filtre qui sera exécuté une fois par requête

    private UtilisateurService utilisateurService;
    private JwtService jwtService;

    public JwtFilter(UtilisateurService utilisateurService, JwtService jwtService) {
        this.utilisateurService = utilisateurService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Jwt tokenDansLaBDD = null;
        String username = null;
        boolean isTokenExpired = true;

        String authorization = request.getHeader("Authorization");
        if(authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.replace("Bearer ", "");
            tokenDansLaBDD = this.jwtService.tokenByValeur(token); // Recherche du token dans la BDD pour verifier si l'utilisateur qui est connecté est bien l'utilisateur qui a créé le token
            isTokenExpired = jwtService.isTokenExpired(token);
            username = jwtService.extractUsername(token);
        }

        if(!isTokenExpired && tokenDansLaBDD.getUtilisateur().getEmail().equals(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.utilisateurService.loadUserByUsername(username); // recuperation des données de l'utilisateur
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); // Creation d'un token d'authentification
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Mise à jour du contexte de sécurité
        }

        filterChain.doFilter(request, response); // Appel du filtre suivant
    }
}
