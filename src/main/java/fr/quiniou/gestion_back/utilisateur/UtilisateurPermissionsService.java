package fr.quiniou.gestion_back.utilisateur;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.quiniou.gestion_back.security.CustomUtilisateurDetails;

@Service
public class UtilisateurPermissionsService {
	  
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    public Page<Utilisateur> obtenirUtilisateurs(Pageable pageable) {
    	return utilisateurRepository.findAll(pageable);
	}

    public Page<UtilisateurDTO> obtenirUtilisateursAutorises(Pageable pageable) {
        Utilisateur currentUser = getCurrentUser();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            // Convertir tous les utilisateurs en DTOs
            return utilisateurRepository.findAll(pageable)
                .map(this::convertToUtilisateurDTO);
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("PROPRIETAIRE"))) {
            List<Long> gestionnaireIds = currentUser.getAppartements().stream()
                .flatMap(appartement -> appartement.getGestionnaires().stream())
                .map(Utilisateur::getId)
                .distinct()
                .collect(Collectors.toList());

            List<UtilisateurDTO> gestionnaireDtos = utilisateurRepository.findByIdIn(gestionnaireIds, pageable)
                .stream()
                .map(this::convertToUtilisateurDTO)
                .collect(Collectors.toList());

            // Ajouter le propriétaire en tant que DTO
            gestionnaireDtos.add(convertToUtilisateurDTO(currentUser));

            return new PageImpl<>(gestionnaireDtos, pageable, gestionnaireDtos.size());
        } else {
            // Convertir l'utilisateur courant en DTO
            return new PageImpl<>(List.of(convertToUtilisateurDTO(currentUser)), pageable, 1);
        }
    }


    public boolean peutGererUtilisateur(Long idUtilisateurCible, String operation) {
        Utilisateur currentUser = getCurrentUser();
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        // Les administrateurs peuvent effectuer n'importe quelle opération
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return true;
        }

        // Pour les autres rôles, vérifiez si l'opération est autorisée sur leur propre compte
        if (idUtilisateurCible.equals(currentUser.getId()) &&
            (operation.equals("obtenir") || operation.equals("mettreAJour") || operation.equals("supprimer"))) {
            return true;
        }

        // Un propriétaire peut gérer les utilisateurs gestionnaires de ses appartements
        if (operation.equals("obtenir") || operation.equals("mettreAJour") || operation.equals("supprimer")) {
            return currentUser.getAppartements().stream()
                .flatMap(appartement -> appartement.getGestionnaires().stream())
                .anyMatch(gestionnaire -> gestionnaire.getId().equals(idUtilisateurCible));
        }

        return false;
    }
    
    private Utilisateur getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUtilisateurDetails) {
            Long userId = ((CustomUtilisateurDetails) principal).getId();
            return utilisateurRepository.findById(userId).orElse(null);
        }

        return null; 
    }

    public UtilisateurDTO convertToUtilisateurDTO(Utilisateur utilisateur) {
    	UtilisateurDTO dto = new UtilisateurDTO();
    	dto.setNom(utilisateur.getNom());
    	dto.setEmail(utilisateur.getEmail());
    	dto.setTelNumero(utilisateur.getTelNumero());
    	return dto;
    	
    }


	public boolean isAdmin() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
    		return true;
        }
        return false;
		
	}


	
}
