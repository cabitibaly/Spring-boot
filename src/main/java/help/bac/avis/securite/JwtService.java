package help.bac.avis.securite;

import help.bac.avis.entite.Jwt;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.repository.JwtRepository;
import help.bac.avis.service.UtilisateurService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@Service
public class JwtService {

    public static final String BEARER = "bearer";
    private final String ENCRYPTION_KEY = "92804f57bc0169e8291b008e786251fbd6c5a289e8824bfaebd61f836301ec0c"; // Clé de chiffrement
    private UtilisateurService utilisateurService;
    private JwtRepository jwtRepository;

    public Jwt tokenByValeur(String valeur) {
        return this.jwtRepository.findByValeur(valeur).orElseThrow(() -> new RuntimeException("Aucun token trouvé"));
    }

    public Map<String, String> generate(String username) { // Génération du token

        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(username); // Chargement de l'utilisateur

        Map<String, String> jwtMap = this.generateJwt(utilisateur);
        final Jwt jwt = Jwt // Creation d'un jwt
                .builder()
                .valeur(jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .utilisateur(utilisateur)
                .build();

        this.jwtRepository.save(jwt);

        return jwtMap;
    }

    // Extraction du username du token
    public String extractUsername(String token) {
        return this.getClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = this.getClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // function prend un objet Claims et renvoie un resultat de type T

    private <T> T getClaim(String token, Function<Claims, T> function) {
        Claims claims = this.getAllClaims(token);
        return function.apply(claims);
    }

    // Extraction de tous les claims
    private Claims getAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Construction du token
    private Map<String, String> generateJwt(Utilisateur utilisateur) {

        final long currentTime = System.currentTimeMillis(); // Temps en millisecondes
        final long expirationTime = currentTime * 30 * 60 * 1000;

        Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, utilisateur.getEmail()
        );

        String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime)) // Date de création du token
                .setExpiration(new Date(expirationTime)) // Date d'expiration du token
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        return Map.of(BEARER, bearer);
    }

    // Création de la clé de signature pour le token
    private Key getKey() {
       final byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

}
