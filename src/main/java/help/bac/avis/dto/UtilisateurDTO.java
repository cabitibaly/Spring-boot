package help.bac.avis.dto;

public record UtilisateurDTO(
                             int id,
                             String nom,
                             String email,
                             Boolean actif,
                             String role) {
}
