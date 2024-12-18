package help.bac.avis.repository;

import help.bac.avis.entite.Conversation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends CrudRepository<Conversation, Integer> {
    @Query("FROM Conversation c WHERE c.users_1 = :user1 AND c.users_2 = :user2 AND c.ticket_id = :ticket_id")
    Optional<Conversation> findByUsers(String user1, String user2, int ticket_id);

    @Query("SELECT DISTINCT CASE " +
            "WHEN c.users_1 = :user THEN c.users_2 " +
            "WHEN c.users_2 = :user THEN c.users_1 " +
            "END " +
            "FROM Conversation c " +
            "WHERE c.users_1 = :user OR c.users_2 = :user")
    List<String> findDistinctUsersByUser(String user);
}
