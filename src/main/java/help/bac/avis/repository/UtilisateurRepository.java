package help.bac.avis.repository;

import help.bac.avis.entite.Utilisateur;
import help.bac.avis.enums.TypeDeRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByEmail(String email); // Recherche par email

    @Query("SELECT u FROM Utilisateur u JOIN u.role r WHERE r.libelle = 'AGENT'")
    List<Utilisateur> findIdsAgents();

    @Query("SELECT u FROM Utilisateur u JOIN u.role r WHERE r.libelle = 'CLIENT'")
    List<Utilisateur> findClients();

    @Query("SELECT u FROM Utilisateur u JOIN u.role r WHERE r.libelle = 'CLIENT' AND u.id = :id")
    Optional<Utilisateur> findClientById(int id);

    @Query("SELECT u FROM Utilisateur u JOIN u.role r WHERE r.libelle = :typeDeRole")
    Optional<Utilisateur> findByRole(TypeDeRole typeDeRole);
}
