package help.bac.avis.service;


import help.bac.avis.entite.Conversation;
import help.bac.avis.repository.ConversationRepository;
import help.bac.avis.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class ConversationService {
    private ConversationRepository conversationRepository;
    private UtilisateurRepository utilisateurRepository;

    public Conversation createConversation(String user1, String user2, int ticket_id) {

        Optional<Conversation> conversationOpt = conversationRepository.findByUsers(user1, user2, ticket_id)
                .or(() -> conversationRepository.findByUsers(user2, user1, ticket_id));

        // Si la conversation existe, la retourner
        if (conversationOpt.isPresent()) {
            return conversationOpt.get();
        }

        // Si la conversation n'existe pas, la créer et la sauvegarder
        Conversation newConversation = new Conversation();
        newConversation.setUsers_1(user1);
        newConversation.setUsers_2(user2);
        newConversation.setTicket_id(ticket_id);
        return conversationRepository.save(newConversation);
    }
}
