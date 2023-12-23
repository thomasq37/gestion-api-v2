package fr.quiniou.gestion_back.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUtilisateurDetails extends User {
    private static final long serialVersionUID = 1L;
	private Long id;

    public CustomUtilisateurDetails(String username, String password, 
                             Collection<? extends GrantedAuthority> authorities, Long id) {
        super(username, password, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
