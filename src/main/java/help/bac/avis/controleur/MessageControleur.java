package help.bac.avis.controleur;

import help.bac.avis.dto.DataDTO;
import help.bac.avis.entite.Conversation;
import help.bac.avis.entite.Message;
import help.bac.avis.repository.ConversationRepository;
import help.bac.avis.repository.MessageRepository;
import help.bac.avis.service.ConversationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@Controller
@AllArgsConstructor
public class MessageControleur {
    private SimpMessagingTemplate simpMessagingTemplate;
    private ConversationRepository conversationRepository;
    private ConversationService conversationService;
    private MessageRepository messageRepository;

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public Message receiveMessage(@Payload Message message){
        return message;
    }

    @MessageMapping("/private-message")
    private Message receivePrivateMessage(@Payload DataDTO message) {
        Conversation conversation = this.conversationService
                .createConversation(message.sender(), message.receiver(), message.ticket_id());

        Message newMessage = new Message();
        newMessage.setSender(message.sender());
        newMessage.setReceiver(message.receiver());
        newMessage.setMessage(message.message());
        newMessage.setDate(Instant.now());
        newMessage.setStatus(message.status());
        newMessage.setConversation(conversation);

        this.messageRepository.save(newMessage);

        simpMessagingTemplate.convertAndSend("/conversation/" + conversation.getId(), newMessage);
        simpMessagingTemplate.convertAndSendToUser(message.receiver(), "/private", newMessage);

        return newMessage;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/conversation")
    @ResponseBody
    public List<Message> getConversation(@RequestParam String user1, @RequestParam String user2, @RequestParam int ticketId ) {
        Conversation conversation = this.conversationService.createConversation(user1, user2, ticketId);
        return this.messageRepository.findByConversation(conversation.getId());
    }
}
