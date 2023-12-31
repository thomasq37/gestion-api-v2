package fr.quiniou.gestion_back.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.quiniou.gestion_back.appartement.AppartementRepository;
import fr.quiniou.gestion_back.appartement.details.AppartementDetails;
import fr.quiniou.gestion_back.appartement.details.AppartementDetailsDTO;
import fr.quiniou.gestion_back.contact.ContactRepository;
import fr.quiniou.gestion_back.frais.FraisRepository;
import fr.quiniou.gestion_back.security.CustomUtilisateurDetails;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@Service
public abstract class PermissionsByRolesService {
	
	// Repository dépendances
	protected final AppartementRepository appartementRepository;
	protected final UtilisateurRepository utilisateurRepository;
	protected final ContactRepository contactRepository;
	protected final FraisRepository fraisRepository;

	// Constantes rôles
	protected static final String ROLE_PROPRIETAIRE = "PROPRIETAIRE";
	protected static final String ROLE_GESTIONNAIRE = "GESTIONNAIRE";
	protected static final String ROLE_ADMIN = "ADMIN";
	protected static final String ROLE_VIEWER = "VIEWER";
	
	// Constantes actions
	protected static final String ACT_OBTENIR = "obtenir";
	protected static final String ACT_MAJ = "maj";
	protected static final String ACT_SUPPRIMER = "supprimer";
	protected static final String ACT_AJOUTER = "ajouter";
	
	public PermissionsByRolesService(
			AppartementRepository appartementRepository,
			UtilisateurRepository utilisateurRepository,
			ContactRepository contactRepository,
			FraisRepository fraisRepository) {
		this.appartementRepository = appartementRepository;
		this.utilisateurRepository = utilisateurRepository;
		this.contactRepository = contactRepository;
		this.fraisRepository = fraisRepository;
	}
    
    public AppartementDetailsDTO convertToAppartementDetailsDTO(AppartementDetails appartementDetails) {
    	AppartementDetailsDTO dto = new AppartementDetailsDTO();
    	if (appartementDetails != null) {
    		dto.setNombrePieces(appartementDetails.getNombrePieces());
        	dto.setSurface(appartementDetails.getSurface());
        	dto.setBalcon(appartementDetails.getBalcon());
    	}
    	return dto;
    }

    // ??


  
	// methodes all	
	protected Utilisateur getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof CustomUtilisateurDetails customUtilisateurDetails) {
	        Long userId = customUtilisateurDetails.getId();
	        return utilisateurRepository.findById(userId).orElse(null);
	    }

		return null;
	}
	
	protected boolean isUserInRole(String role) {
	    Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
	    return authorities.stream().anyMatch(a -> a.getAuthority().equals(role));
	}	
}
