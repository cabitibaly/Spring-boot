package help.bac.avis.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ticket")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String titre;
    private String description;
    private Instant dateCreation;
    private Instant dateFermeture;
    private String pieceJointe;
    @ManyToOne
    @JoinColumn(name = "client")
    private Utilisateur client;
    @ManyToOne
    @JoinColumn(name = "agent")
    private Utilisateur agent;
}
