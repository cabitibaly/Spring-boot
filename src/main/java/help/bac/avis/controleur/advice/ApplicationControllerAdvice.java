package help.bac.avis.controleur.advice;


import io.jsonwebtoken.security.MalformedKeyException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = BadCredentialsException.class) // Exception pour les erreurs de connexion
    public @ResponseBody ProblemDetail badCredentialsException(final BadCredentialsException exception) {
//        log.error(exception.getMessage(), exception);
//        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(UNAUTHORIZED, "Identifiants incorrects");// Explique le probleme qu'on a
        problemDetail.setProperty("erreur", "Nous n'avons pas pu vous authentifier");
        return problemDetail;
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = {SignatureException.class, MalformedKeyException.class}) // Exception pour les erreurs de signature
    public @ResponseBody ProblemDetail badCredentialsException(final Exception exception) {
//        log.error(exception.getMessage(), exception);
        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(UNAUTHORIZED, "Votre jeton n'est pas valide");// Explique le probleme qu'on a
        problemDetail.setProperty("erreur", "Quelque s'est mal passé :(");
        return problemDetail;
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(value =  AccessDeniedException.class) // Exception pour les erreurs de connexion
    public @ResponseBody ProblemDetail badCredentialsException(final AccessDeniedException exception) {
//        log.error(exception.getMessage(), exception);
//        ApplicationControllerAdvice.log.error(exception.getMessage(), exception);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(FORBIDDEN, "Vos droits ne vous permettent pas d'accéder à cette ressource");// Explique le probleme qu'on a
        problemDetail.setProperty("erreur", "Vous n'êtes pas connectés sur le bon compte");
        return problemDetail;
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(value = Exception.class)
    public Map<String, String> exceptionHandler() {
        return Map.of("erreur", "Quelque s'est mal passé :(");
    }

}
