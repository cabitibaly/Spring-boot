package help.bac.avis.repository;

import help.bac.avis.entite.Validation;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;

    public interface ValidationRepository extends CrudRepository<Validation, Integer> {

        Optional<Validation> findByCode(String code);

        void deleteAllByExpirationBefore(Instant now);
    }
