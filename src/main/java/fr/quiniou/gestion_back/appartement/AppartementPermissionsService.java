package fr.quiniou.gestion_back.appartement;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.quiniou.gestion_back.appartement.dto.AppartementDTOForAdmin;
import fr.quiniou.gestion_back.appartement.dto.AppartementDTOForGestionnaire;
import fr.quiniou.gestion_back.appartement.dto.AppartementDTOForProprietaire;
import fr.quiniou.gestion_back.appartement.dto.AppartementDTOForViewer;
import fr.quiniou.gestion_back.auth.PermissionsByRolesService;
import fr.quiniou.gestion_back.contact.Contact;
import fr.quiniou.gestion_back.contact.ContactRepository;
import fr.quiniou.gestion_back.frais.Frais;
import fr.quiniou.gestion_back.frais.FraisRepository;
import fr.quiniou.gestion_back.periode_location.PeriodeLocation;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@Service
public class AppartementPermissionsService extends PermissionsByRolesService {

	public AppartementPermissionsService(
			AppartementRepository appartementRepository,
			UtilisateurRepository utilisateurRepository, 
			ContactRepository contactRepository, 
			FraisRepository fraisRepository) {
		super(appartementRepository, utilisateurRepository, contactRepository, fraisRepository);
	}

	public Page<Object> obtenirAppartementsAutorises(Pageable pageable) {
		Utilisateur currentUser = getCurrentUser();
		// Vérifiez les rôles et appliquez les filtres appropriés
		if (isUserInRole(ROLE_ADMIN)) {
			// Les admins peuvent voir tous les appartements
			return this.appartementRepository.findAll(pageable).map(this::convertToAppartementDTOForAdmin);
		} else if (isUserInRole(ROLE_PROPRIETAIRE)) {
			// Les propriétaires ne peuvent voir que leurs appartements
			return appartementRepository.findByProprietaire(currentUser, pageable)
					.map(this::convertToAppartementDTOForProprietaire);
		} else if (isUserInRole(ROLE_GESTIONNAIRE)) {
			// Les gestionnaires peuvent voir les appartements qu'ils gèrent
			return appartementRepository.findByGestionnairesContains(currentUser, pageable)
					.map(this::convertToAppartementDTOForGestionnaire);
		} else {
			// Les viewers et autres rôles ne peuvent voir que les appartements publics
			return appartementRepository.findByPublicAppartementIsTrue(pageable)
					.map(this::convertToAppartementDTOForViewer);
		}
	}

	public Object obtenirAppartementAutoriseParId(Long id) {
		Appartement appartement = appartementRepository.findById(id).orElse(null);

		// Si l'appartement n'est pas trouvé
		if (appartement == null) {
			return null;
		}

		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication()
				.getAuthorities();

		if (authorities.stream().anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN))) {
			return convertToAppartementDTOForAdmin(appartement);
		} else if (authorities.stream().anyMatch(a -> a.getAuthority().equals(ROLE_PROPRIETAIRE))
				&& hasOwnedPermission(id)) {
			return convertToAppartementDTOForProprietaire(appartement);
		} else if (authorities.stream().anyMatch(a -> a.getAuthority().equals(ROLE_GESTIONNAIRE))
				&& hasManagedPermission(id)) {
			return convertToAppartementDTOForGestionnaire(appartement);
		} else if (appartement.getPublicAppartement()
				&& authorities.stream().anyMatch(a -> a.getAuthority().equals(ROLE_VIEWER))) {
			return convertToAppartementDTOForViewer(appartement);
		}

		return null;
	}

	public boolean hasOwnedPermission(Long appartementId) {
		Utilisateur currentUser = getCurrentUser();
		Optional<Appartement> appartementOpt = appartementRepository.findById(appartementId);

		if (!appartementOpt.isPresent()) {
			return false;
		}

		Appartement appartement = appartementOpt.get();
		return appartement.getProprietaire().equals(currentUser);
	}

	public boolean hasManagedPermission(Long appartementId) {
		Utilisateur currentUser = getCurrentUser();
		Optional<Appartement> appartementOpt = appartementRepository.findById(appartementId);

		if (!appartementOpt.isPresent()) {
			return false;
		}

		Appartement appartement = appartementOpt.get();
		return appartement.getGestionnaires().contains(currentUser);
	}

	public boolean peutGererAppartement(Appartement appartement, Long appartementId, String operation) {
		Utilisateur currentUser = getCurrentUser();
		// Administrateurs
		if (isUserInRole(ROLE_ADMIN)) {
			return true;
		}

		// Propriétaires
		if (isUserInRole(ROLE_PROPRIETAIRE)) {
			if (ACT_AJOUTER.equals(operation)) {
				return appartement != null && appartement.getProprietaire().getId().equals(currentUser.getId());
			} else if (ACT_SUPPRIMER.equals(operation) || ACT_MAJ.equals(operation) || ACT_OBTENIR.equals(operation)) {
				return appartementRepository.findById(appartementId)
						.map(appartementATrouver -> appartementATrouver.getProprietaire().equals(currentUser))
						.orElse(false);
			}
		}

		// Autres rôles
		return false;
	}

	public AppartementDTOForAdmin convertToAppartementDTOForAdmin(Appartement appartement) {
		AppartementDTOForAdmin dto = new AppartementDTOForAdmin();
		dto.setId(appartement.getId());
		dto.setProprietaireId(appartement.getProprietaire().getId());
		dto.setAdresse(appartement.getAdresse());
		dto.setDetails(appartement.getDetails());
		dto.setStatistiques(appartement.getStatistiques());
		dto.setPublicAppartement(appartement.getPublicAppartement());
		List<Long> contactIds = appartement.getContacts().stream().map(Contact::getId).collect(Collectors.toList());
		dto.setContactIds(contactIds);
		List<Long> gestionnaireIds = appartement.getGestionnaires().stream().map(Utilisateur::getId)
				.collect(Collectors.toList());
		dto.setGestionnaireIds(gestionnaireIds);
		List<Long> fraisFixeIds = appartement.getFraisFixes().stream().map(Frais::getId).collect(Collectors.toList());
		dto.setFraisFixeIds(fraisFixeIds);
		List<Long> periodeLocationIds = appartement.getPeriodesLocations().stream().map(PeriodeLocation::getId)
				.collect(Collectors.toList());
		dto.setPeriodeLocationIds(periodeLocationIds);
		return dto;
	}

	public AppartementDTOForProprietaire convertToAppartementDTOForProprietaire(Appartement appartement) {
		AppartementDTOForProprietaire dto = new AppartementDTOForProprietaire();
		dto.setProprietaireId(appartement.getProprietaire().getId());
		dto.setAdresse(appartement.getAdresse());
		dto.setDetails(appartement.getDetails());
		dto.setStatistiques(appartement.getStatistiques());
		dto.setPublicAppartement(appartement.getPublicAppartement());
		List<Long> contactIds = appartement.getContacts().stream().map(Contact::getId).collect(Collectors.toList());
		dto.setContactIds(contactIds);
		List<Long> gestionnaireIds = appartement.getGestionnaires().stream().map(Utilisateur::getId)
				.collect(Collectors.toList());
		dto.setGestionnaireIds(gestionnaireIds);
		List<Long> fraisFixeIds = appartement.getFraisFixes().stream().map(Frais::getId).collect(Collectors.toList());
		dto.setFraisFixeIds(fraisFixeIds);
		List<Long> periodeLocationIds = appartement.getPeriodesLocations().stream().map(PeriodeLocation::getId)
				.collect(Collectors.toList());
		dto.setPeriodeLocationIds(periodeLocationIds);
		return dto;
	}

	public AppartementDTOForGestionnaire convertToAppartementDTOForGestionnaire(Appartement appartement) {
		AppartementDTOForGestionnaire dto = new AppartementDTOForGestionnaire();
		dto.setProprietaireId(appartement.getProprietaire().getId());
		dto.setAdresse(appartement.getAdresse());
		dto.setDetails(appartement.getDetails());
		List<Long> contactIds = appartement.getContacts().stream().map(Contact::getId).collect(Collectors.toList());
		dto.setContactIds(contactIds);
		List<Long> periodeLocationIds = appartement.getPeriodesLocations().stream().map(PeriodeLocation::getId)
				.collect(Collectors.toList());
		dto.setPeriodeLocationIds(periodeLocationIds);
		return dto;
	}

	public AppartementDTOForViewer convertToAppartementDTOForViewer(Appartement appartement) {
		AppartementDTOForViewer dto = new AppartementDTOForViewer();
		dto.setProprietaireId(appartement.getProprietaire().getId());
		dto.setAdresse(appartement.getAdresse());
		dto.setDetails(appartement.getDetails());
		List<Long> contactIds = appartement.getContacts().stream().map(Contact::getId).collect(Collectors.toList());
		dto.setContactIds(contactIds);
		return dto;
	}

}
