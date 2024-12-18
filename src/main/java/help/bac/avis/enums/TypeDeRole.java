package help.bac.avis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static help.bac.avis.enums.TypePermission.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@AllArgsConstructor
public enum TypeDeRole {
    UTILISATEUR(
            Set.of(
                    UTILISATEUR_CREATE,
                    UTILISATEUR_READ,
                    UTILISATEUR_UPDATE,
                    UTILISATEUR_DELETE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE_AVIS
            )
    ),
    ADMINISTRATEUR(
            Set.of(
                    ADMINISTRATEUR_CREATE,
                    ADMINISTRATEUR_READ,
                    ADMINISTRATEUR_UPDATE,
                    ADMINISTRATEUR_DELETE,

                    MANAGER_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE_AVIS
            )
    );

    @Getter
    Set<TypePermission> permissions;

    // Gestion des autorisations en fonction des permissions et du nom de l'utilisateur
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> grantedAuthorities = this.getPermissions().stream().map(
                permission -> new SimpleGrantedAuthority(permission.name())
        ).collect(Collectors.toList());

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return grantedAuthorities;
    }
}
