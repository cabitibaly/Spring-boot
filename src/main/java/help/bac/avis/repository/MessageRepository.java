package help.bac.avis.repository;

import help.bac.avis.entite.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<Message, Integer> {

    @Query("FROM Message m WHERE m.conversation.id = :id")
    List<Message> findByConversation(int id);

//    @Query("FROM Message m WHERE m.sender = :user or m.receiver = :user order by m.date desc")
    @Query("FROM Message m WHERE m.sender = :user OR m.receiver = :user ORDER BY m.date DESC")
    List<Message> findByUser(String user);

    @Override
//    @Query("FROM Message WHERE id = :id")
    Optional<Message> findById(Integer id);
}
