package fr.quiniou.gestion_back.security;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@Service
public class CustomUtilisateurDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;
    private static final String DEFAULT_ROLE = "VIEWER";

    public CustomUtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String nom) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByNom(nom)
            .orElseThrow(() -> new UsernameNotFoundException("L'utilisateur n'a pas été trouvé : " + nom));

        // Créer une liste de GrantedAuthority à partir des rôles de l'utilisateur
        /*List<GrantedAuthority> authorities = utilisateur.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNom())) // Assurez-vous que getNom() renvoie le nom du rôle
                .collect(Collectors.toList());*/
        List<GrantedAuthority> authorities;
        if (utilisateur.getRoles().isEmpty()) {
            // Attribuer le rôle par défaut si aucun rôle n'est trouvé
            authorities = Collections.singletonList(new SimpleGrantedAuthority(DEFAULT_ROLE));
        } else {
            // Sinon, utiliser les rôles existants
            authorities = utilisateur.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getNom()))
                .collect(Collectors.toList());
        }

        return new CustomUtilisateurDetails(
            utilisateur.getNom(),
            utilisateur.getMdp(),
            authorities,
            utilisateur.getId());
    }
}
