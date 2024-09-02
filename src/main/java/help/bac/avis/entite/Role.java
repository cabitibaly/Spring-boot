package help.bac.avis.entite;

import help.bac.avis.enums.TypeDeRole;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING) // EnumType.STRING permet de stocker les roles sous forme de chaîne de caractères
    @Column(length = 20)
    private TypeDeRole libelle;
}
