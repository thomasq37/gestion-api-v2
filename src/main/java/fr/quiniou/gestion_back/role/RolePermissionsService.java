package fr.quiniou.gestion_back.role;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionsService {

	public boolean peutGererRoles() {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();

		// Les administrateurs peuvent effectuer n'importe quelle opération
		if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
			return true;
		}

		// Les autres rôles n'ont pas le droit d'ajouter ou de supprimer des appartements
		return false;
	}
}
