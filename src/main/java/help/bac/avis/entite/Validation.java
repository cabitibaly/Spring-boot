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
@Table(name = "validation")
public class Validation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Instant creation; // Date de création
    private Instant expiration; // Date d'expiration
    private Instant activation; // Date d'activation
    private String code;
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.DETACH})
    private Utilisateur utilisateur;
}
