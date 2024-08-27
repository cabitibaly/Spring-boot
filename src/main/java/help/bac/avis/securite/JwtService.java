package help.bac.avis.securite;

import help.bac.avis.entite.Utilisateur;
import help.bac.avis.service.UtilisateurService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@Service
public class JwtService {

    private final String ENCRYPTION_KEY = "92804f57bc0169e8291b008e786251fbd6c5a289e8824bfaebd61f836301ec0c";
    private UtilisateurService utilisateurService;

    public Map<String, String> generate(String username) { // Génération du token

        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(username);

        return this.generateJwt(utilisateur);
    }

    private Map<String, String> generateJwt(Utilisateur utilisateur) {

        Map<String, String> claims = Map.of(
                "nom", utilisateur.getNom(),
                "email", utilisateur.getEmail()
        );

        final long currentTime = System.currentTimeMillis(); // Temps en millisecondes
        final long expirationTime = currentTime * 30 * 60 * 1000;

        String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(expirationTime))
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        return Map.of("bearer", bearer);
    }

    // Création de la clé de signature pour le token
    private Key getKey() {
       final byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }
}
