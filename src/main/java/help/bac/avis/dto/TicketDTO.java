package help.bac.avis.dto;

import jakarta.annotation.Nullable;

import java.time.Instant;

public record TicketDTO(int id,
                        String titre,
                        String description,
                        Instant dateCreation,
                        Instant dateFermeture,
                        String clientNom,
                        @Nullable String agent,
                        @Nullable String pieceJointe
                        ) {
}
