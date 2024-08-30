package help.bac.avis.repository;

import help.bac.avis.entite.Jwt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.stream.Stream;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {
    Optional<Jwt> findByValeurAndDesactiveAndExpire(String valeur, boolean desactive, boolean expire);

    @Query("FROM Jwt j WHERE j.expire = :expire AND j.desactive = :desactive AND j.utilisateur.email = :email")
    Optional<Jwt> findUtilisateurValidToken(String email, boolean desactive, boolean expire);

    @Query("FROM Jwt j WHERE j.utilisateur.email = :email")
    Stream<Jwt> findUtilisateur(String email);

    void deleteAllByExpireAndDesactive(boolean expire, boolean desactive);
}
