package help.bac.avis.securite;

import help.bac.avis.dto.UtilisateurDTO;
import help.bac.avis.entite.Jwt;
import help.bac.avis.entite.RefreshToken;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.repository.JwtRepository;
import help.bac.avis.service.UtilisateurService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@AllArgsConstructor
@Service
public class JwtService {

    public static final String BEARER = "bearer";
    public static final String REFRESH = "refresh";
    private final String ENCRYPTION_KEY = "92804f57bc0169e8291b008e786251fbd6c5a289e8824bfaebd61f836301ec0c"; // Clé de chiffrement
    private UtilisateurService utilisateurService;
    private JwtRepository jwtRepository;

    public Jwt tokenByValeur(String valeur) {
        return this.jwtRepository.findByValeurAndDesactiveAndExpire(
                valeur,
                false,
                false
        ).orElseThrow(() -> new RuntimeException("Token invalide ou inconnu"));
    }

    public Map<String, Object> generate(String username) { // Génération du token

        Utilisateur utilisateur = (Utilisateur) this.utilisateurService.loadUserByUsername(username); // Chargement de l'utilisateur
        this.disableTokens(utilisateur); // Désactivation des tokens de l'utilisateur

        RefreshToken refreshToken = RefreshToken.builder()
                .valeur(UUID.randomUUID().toString())
                .expire(false)
                .creation(Instant.now())
                .expiration(Instant.now().plusMillis(360 * 60 * 1000))
                .build();

        Map<String, Object> jwtMap = new java.util.HashMap<>(this.generateJwt(utilisateur));
        final Jwt jwt = Jwt // Creation d'un jwt
                .builder()
                .valeur((String) jwtMap.get(BEARER))
                .desactive(false)
                .expire(false)
                .utilisateur(utilisateur)
                .refreshToken(refreshToken)
                .build();

        this.jwtRepository.save(jwt);

        jwtMap.put(REFRESH, refreshToken.getValeur());

        return jwtMap;
    }

    // Désactivation des tokens de l'utilisateur
    private void disableTokens(Utilisateur utilisateur) {
        final List<Jwt> jwtList = this.jwtRepository.findUtilisateur(utilisateur.getEmail()).peek(
                jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpire(true);
                }
        ).collect(Collectors.toList());

        this.jwtRepository.saveAll(jwtList);
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
    private Map<String, Object> generateJwt(Utilisateur utilisateur) {

        final long currentTime = System.currentTimeMillis(); // Temps en millisecondes
        final long expirationTime = currentTime + 360 * 60 * 1000;

        Map<String, Object> claims = Map.of(
                "nom", utilisateur.getNom(),
                Claims.EXPIRATION, new Date(expirationTime),
                Claims.SUBJECT, utilisateur.getEmail()
        );

        UtilisateurDTO utilisateurDTO = new UtilisateurDTO(
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.isActif(),
                utilisateur.getRole().getLibelle().toString()
        );

        String bearer = Jwts.builder()
                .setIssuedAt(new Date(currentTime)) // Date de création du token
                .setExpiration(new Date(expirationTime)) // Date d'expiration du token
                .setSubject(utilisateur.getEmail())
                .setClaims(claims)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();

        return Map.of(BEARER, bearer, "utilisateur", utilisateurDTO, "satus", 200);
    }

    // Création de la clé de signature pour le token
    private Key getKey() {
       final byte[] decoder = Decoders.BASE64.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(decoder);
    }

    public void deconnexion() {
        Utilisateur utilisateur = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt jwt = this.jwtRepository.findUtilisateurValidToken(
                utilisateur.getEmail(),
                false,
                false
        ).orElseThrow(() -> new RuntimeException("TOKEN_INVALIDE"));
        jwt.setExpire(true);
        jwt.setDesactive(true);
        this.jwtRepository.save(jwt);
    }

    // Suppression des tokens qui ont expiré
    @Scheduled(cron = "0 */1 * * * *")
    public void removeUseLess() {
        this.jwtRepository.deleteAllByExpireAndDesactive(true, true);
    }

    // Rafraichissement du token
    public Map<String, Object> refreshToken(Map<String, String> refreshTokenRequest) {

        Jwt jwt = this.jwtRepository.findByRefreshToken(refreshTokenRequest.get(REFRESH)).orElseThrow(() -> new RuntimeException("Refresh token invalide"));

        if(jwt.getRefreshToken().isExpire() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token invalide");
        }
        this.disableTokens(jwt.getUtilisateur());
        log.info("Refresh token : " + Instant.now().toString());
        return this.generate(jwt.getUtilisateur().getEmail());
    }
}
