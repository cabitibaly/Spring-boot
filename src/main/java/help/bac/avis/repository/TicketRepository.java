package help.bac.avis.repository;

import help.bac.avis.entite.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Query("FROM Ticket")
    List<Ticket> findAll();

//    Optional<Ticket> findByIntitule(String intitule);

    @Query("FROM Ticket WHERE client.id = :id")
    Page<Ticket> findAllByClient(int id, Pageable pageable);

    @Query("FROM Ticket WHERE agent.id = :id")
    Page<Ticket> findAllByAgent(int id, Pageable pageable);

    @Query("FROM Ticket WHERE agent.id = :agentId AND id = :id")
    Optional<Ticket> findByAgentAndTicket(int agentId, int id);

    @Query("FROM Ticket WHERE client.id = :clientId AND id = :id")
    Optional<Ticket> findByClientAndTicket(int clientId, int id);
}
