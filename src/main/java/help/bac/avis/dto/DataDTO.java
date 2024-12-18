package help.bac.avis.dto;

import help.bac.avis.enums.Status;

public record DataDTO(
        int id,
        String message,
        String sender,
        String receiver,
        Status status,
        int conversation_id,
        int ticket_id
) {
}
