package fr.quiniou.gestion_back.appartement;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import fr.quiniou.gestion_back.security.CustomUtilisateurDetails;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurDTO;
import fr.quiniou.gestion_back.utilisateur.UtilisateurPermissionsService;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@Service
public class AppartementPermissionsService {

	@Autowired
	private AppartementRepository appartementRepository;

	@Autowired
	private UtilisateurRepository utilisateurRepository;

	@Autowired
	private UtilisateurPermissionsService utilisateurPermissionsService;

	public Page<AppartementDTO> obtenirAppartementsAutorises(Pageable pageable) {
		Utilisateur currentUser = getCurrentUser();
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();
		// Vérifiez les rôles et appliquez les filtres appropriés
		if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
			// Les admins peuvent voir tous les appartements
			return appartementRepository.findAll(pageable).map(this::convertToAppartementDTO);
		} else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("PROPRIETAIRE"))) {
			// Les propriétaires ne peuvent voir que leurs appartements
			return appartementRepository.findByProprietaire(currentUser, pageable).map(this::convertToAppartementDTO);
		} else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("GESTIONNAIRE"))) {
			// Les gestionnaires peuvent voir les appartements qu'ils gèrent
			return appartementRepository.findByGestionnairesContains(currentUser, pageable)
					.map(this::convertToAppartementDTO);
		} else {
			// Les viewers et autres rôles ne peuvent voir que les appartements publics
			return appartementRepository.findByPublicAppartementIsTrue(pageable).map(this::convertToAppartementDTO);
		}
	}

	public AppartementDTO obtenirAppartementAutoriseParId(Long id) {
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appartement non trouvé"));

		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();

		// Vérifier les permissions en fonction du rôle
		if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))
				|| (authorities.stream().anyMatch(a -> a.getAuthority().equals("PROPRIETAIRE"))
						&& hasOwnedPermission(id))
				|| (authorities.stream().anyMatch(a -> a.getAuthority().equals("GESTIONNAIRE"))
						&& hasManagedPermission(id))
				|| (appartement.getPublicAppartement()
						&& authorities.stream().anyMatch(a -> a.getAuthority().equals("VIEWER")))) {
			return convertToAppartementDTO(appartement);
		} else {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
	}

	public boolean peutGererAppartement(Appartement appartement, Long appartementId, String operation) {
		Utilisateur currentUser = getCurrentUser();
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();

		// Les administrateurs peuvent effectuer n'importe quelle opération
		if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
			return true;
		}

		// Les propriétaires peuvent ajouter ou supprimer un appartement s'ils en sont
		if (authorities.stream().anyMatch(a -> a.getAuthority().equals("PROPRIETAIRE"))) {
			if ("ajouter".equals(operation)) {
				return appartement != null && appartement.getProprietaire().getId().equals(currentUser.getId());
			} else if ("supprimer".equals(operation) || "maj".equals(operation) || "obtenir".equals(operation)) {
				Appartement appartementATrouver = appartementRepository.findById(appartementId)
						.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appartement non trouvé"));
				return appartementATrouver.getProprietaire().equals(currentUser);
			}
		}

		// Les autres rôles n'ont pas le droit d'ajouter ou de supprimer des
		// appartements
		return false;
	}

	public boolean hasOwnedPermission(Long appartementId) {
		Utilisateur currentUser = getCurrentUser();
		Appartement appartement = appartementRepository.findById(appartementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appartement non trouvé"));
		return appartement.getProprietaire().equals(currentUser);
	}

	public boolean hasManagedPermission(Long appartementId) {
		Utilisateur currentUser = getCurrentUser();
		Appartement appartement = appartementRepository.findById(appartementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appartement non trouvé"));
		return appartement.getGestionnaires().contains(currentUser);
	}

	private Utilisateur getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof CustomUtilisateurDetails) {
			Long userId = ((CustomUtilisateurDetails) principal).getId();
			return utilisateurRepository.findById(userId).orElse(null);
		}

		return null;
	}

	public AppartementDTO convertToAppartementDTO(Appartement appartement) {
		AppartementDTO dto = new AppartementDTO();
		dto.setProprietaire(utilisateurPermissionsService.convertToUtilisateurDTO(appartement.getProprietaire()));
		dto.setContacts(appartement.getContacts());
		List<UtilisateurDTO> gestionnaireDTOs = appartement.getGestionnaires().stream()
				.map(utilisateurPermissionsService::convertToUtilisateurDTO).collect(Collectors.toList());
		dto.setGestionnaires(gestionnaireDTOs);
		dto.setAdresse(appartement.getAdresse());
		return dto;

	}
}
