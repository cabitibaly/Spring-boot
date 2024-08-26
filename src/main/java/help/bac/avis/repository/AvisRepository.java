package help.bac.avis.repository;

import help.bac.avis.entite.Avis;
import org.springframework.data.repository.CrudRepository;

public interface AvisRepository extends CrudRepository<Avis, Integer> { // Avis c'est la table et Integer c'est le type de la clé
}
