package help.bac.avis.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @JoinColumn(name = "users_1")
    private String users_1;
    @JoinColumn(name = "users_2")
    private String users_2;
    @JoinColumn(name = "ticket_id")
    private int ticket_id;
}
