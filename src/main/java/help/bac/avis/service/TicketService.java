package help.bac.avis.service;

import help.bac.avis.dto.TicketDTO;
import help.bac.avis.entite.Ticket;
import help.bac.avis.entite.Utilisateur;
import help.bac.avis.enums.TypeDeRole;
import help.bac.avis.repository.TicketRepository;
import help.bac.avis.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class TicketService {
    private TicketRepository ticketRepository;
    private UtilisateurRepository utilisateurRepository;
    private int positionCourante = 0; // représente la dernière personne a qui le dernier ticket a été assigné

    public TicketService(UtilisateurRepository utilisateurRepository, TicketRepository ticketRepository) {
        this.positionCourante = 0;
        this.utilisateurRepository = utilisateurRepository;
        this.ticketRepository = ticketRepository;
    }

    public Utilisateur getAgent() {
        List<Utilisateur> agents = this.utilisateurRepository.findIdsAgents();

        if (agents.isEmpty()) {
            return this.utilisateurRepository.findByRole(TypeDeRole.ADMINISTRATEUR)
                    .orElseThrow(EntityNotFoundException::new);
        }

        Utilisateur agent = agents.get(this.positionCourante);
        this.positionCourante = (this.positionCourante + 1) % agents.size();

        return agent;
    }

    public void ajouterUnTicket(TicketDTO ticketDTO, Utilisateur client) {
        if(ticketDTO.titre().isEmpty()) {
            throw new RuntimeException("Le titre du ticket est obligatoire");
        }

        Ticket ticket = new Ticket();
        ticket.setTitre(ticketDTO.titre());
        ticket.setDescription(ticketDTO.description());
        ticket.setDateCreation(Instant.now());
        ticket.setClient(client);
        ticket.setAgent(this.getAgent());
        ticket.setPieceJointe(ticketDTO.pieceJointe());
        this.ticketRepository.save(ticket);
    }

    public TicketDTO getTicket(int id) {
        Ticket ticket = this.ticketRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket non trouvé"));

        return new TicketDTO(
                ticket.getId(),
                ticket.getTitre(),
                ticket.getDescription(),
                ticket.getDateCreation(),
                ticket.getDateFermeture(),
                ticket.getClient().getEmail(),
                ticket.getAgent().getEmail(),
                ticket.getPieceJointe()
        );
    }

    public Page<TicketDTO> ListeTickets(int page, int size, String sort, Boolean direction) {
        Sort sortBy = direction ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Page<Ticket> tickets = this.ticketRepository.findAll(PageRequest.of(page, size, sortBy));

        return tickets.map(ticket -> new TicketDTO(
                        ticket.getId(),
                        ticket.getTitre(),
                        ticket.getDescription(),
                        ticket.getDateCreation(),
                        ticket.getDateFermeture(),
                        ticket.getClient().getEmail(),
                        ticket.getAgent().getEmail(),
                        ticket.getPieceJointe()
                ));
    }

//    public Page<TicketDTO> ListeTicketsAgent(int id, int page, int size, String sort, Boolean direction) {
//        Sort sortBy = direction ? Sort.by(sort).ascending() : Sort.by(sort).descending();
//        Page<Ticket> tickets = this.ticketRepository.findAllByAgent(id, PageRequest.of(page, size, sortBy));
//
//        return tickets
//                .map(ticket -> new TicketDTO(
//                        ticket.getId(),
//                        ticket.getTitre(),
//                        ticket.getDescription(),
//                        ticket.getStatut().toString(),
//                        ticket.getPriorite() == null ? "" : ticket.getPriorite().toString(),
//                        ticket.getDateCreation(),
//                        ticket.getDateFermeture(),
//                        ticket.getClient().getNom(),
//                        ticket.getClient().getPrenom(),
//                        ticket.getAgent().getNom(),
//                        ticket.getType().getIntitule(),
//                        ticket.getPieceJointe()
//                ));
//    }

//    public TicketDTO getAgentTicket(int agentId, int id) {
//        Ticket ticket = this.ticketRepository
//                .findByAgentAndTicket(agentId, id)
//                .orElseThrow(() -> new EntityNotFoundException());
//
//        return new TicketDTO(
//                ticket.getId(),
//                ticket.getTitre(),
//                ticket.getDescription(),
//                ticket.getStatut().toString(),
//                ticket.getPriorite() == null ? "" : ticket.getPriorite().toString(),
//                ticket.getDateCreation(),
//                ticket.getDateFermeture(),
//                ticket.getClient().getNom(),
//                ticket.getClient().getPrenom(),
//                ticket.getAgent().getNom(),
//                ticket.getType().getIntitule(),
//                ticket.getPieceJointe()
//        );
//    }

    public Page<TicketDTO> ListeTicketsClient(int id, int page, int size, String sort, Boolean direction) {
        Sort sortBy = direction ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        Page<Ticket> tickets = this.ticketRepository.findAllByClient(id, PageRequest.of(page, size, sortBy));

        return tickets
                .map(ticket -> new TicketDTO(
                        ticket.getId(),
                        ticket.getTitre(),
                        ticket.getDescription(),
                        ticket.getDateCreation(),
                        ticket.getDateFermeture(),
                        ticket.getClient().getEmail(),
                        ticket.getAgent().getEmail(),
                        ticket.getPieceJointe()
                ));
    }

    public TicketDTO getClientTicket(int clientId, int id) {
        Ticket ticket = this.ticketRepository
                .findByClientAndTicket(clientId, id)
                .orElseThrow(EntityNotFoundException::new);

        return new TicketDTO(
                ticket.getId(),
                ticket.getTitre(),
                ticket.getDescription(),
                ticket.getDateCreation(),
                ticket.getDateFermeture(),
                ticket.getClient().getEmail(),
                ticket.getAgent().getEmail(),
                ticket.getPieceJointe()
        );
    }
}
