package help.bac.avis.controleur;


import help.bac.avis.dto.TicketDTO;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.securite.JwtService;
import help.bac.avis.service.TicketService;
import help.bac.avis.service.UtilisateurService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/tickets")
public class TicketControleur {
    private TicketService ticketService;
    private JwtService jwtService;
    private UtilisateurService utilisateurService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/creer-un-ticket")
    public Map<String, Object> creerUnTicket(@RequestBody TicketDTO ticketDTO, @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String email = this.jwtService.extractUsername(token);
        Utilisateur client = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
        this.ticketService.ajouterUnTicket(ticketDTO, client);
        return Map.of("message", "Ticket créé avec succès", "status", 201);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tous-les-tickets")
    public Page<TicketDTO> tousLesTickets(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "8") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "true") Boolean direction
    ) {
        String token = authorization.replace("Bearer ", "");
        String email = this.jwtService.extractUsername(token);
        Utilisateur agent = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
//        if(agent.getRole().getLibelle().toString().equals("AGENT")) {
//            return this.ticketService.ListeTicketsAgent(agent.getId(), page, size, sort, direction);
//        }
        return this.ticketService.ListeTickets(page, size, sort, direction);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tous-les-tickets/{id}")
    public TicketDTO getTicket(@PathVariable int id, @RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        String email = this.jwtService.extractUsername(token);
        Utilisateur agent = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
//        if(agent.getRole().getLibelle().toString().equals("AGENT")) {
//            return this.ticketService.getAgentTicket(agent.getId(), id);
//        }
        return this.ticketService.getTicket(id);
    }

//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/tous-les-tickets-agent")
//    public Page<TicketDTO> tousLesTicketsAgent(
//            @RequestHeader("Authorization") String authorization,
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "10") int size,
//            @RequestParam(name = "sort", defaultValue = "id") String sort,
//            @RequestParam(name = "direction", defaultValue = "true") Boolean direction
//    ) {
//        String token = authorization.replace("Bearer ", "");
//        String email = this.jwtService.extractUsername(token);
//        Utilisateur agent = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
//        return this.ticketService.ListeTicketsAgent(agent.getId(), page, size, sort, direction);
//    }

//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/tous-les-tickets-agent/{id}")
//    public TicketDTO getAgentTicket(@RequestHeader("Authorization") String authorization, @PathVariable int id) {
//        String token = authorization.replace("Bearer ", "");
//        String email = this.jwtService.extractUsername(token);
//        Utilisateur agent = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
//        return this.ticketService.getAgentTicket(agent.getId(), id);
//    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tous-les-tickets-client")
    public Page<TicketDTO> tousLesTicketsClient(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "true") Boolean direction
    ) {
        String token = authorization.replace("Bearer ", "");
        String email = this.jwtService.extractUsername(token);
        Utilisateur client = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
        return this.ticketService.ListeTicketsClient(client.getId(), page, size, sort, direction);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/tous-les-tickets-client/{id}")
    public TicketDTO tousLesTicketsClient(@RequestHeader("Authorization") String authorization, @PathVariable int id) {
        String token = authorization.replace("Bearer ", "");
        String email = this.jwtService.extractUsername(token);
        Utilisateur client = (Utilisateur) this.utilisateurService.loadUserByUsername(email);
        return this.ticketService.getClientTicket(client.getId(), id);
    }

}
