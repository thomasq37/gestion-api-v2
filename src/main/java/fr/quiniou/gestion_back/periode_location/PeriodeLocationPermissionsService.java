package fr.quiniou.gestion_back.periode_location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.quiniou.gestion_back.appartement.Appartement;
import fr.quiniou.gestion_back.appartement.AppartementRepository;
import fr.quiniou.gestion_back.auth.PermissionsByRolesService;
import fr.quiniou.gestion_back.contact.ContactRepository;
import fr.quiniou.gestion_back.frais.Frais;
import fr.quiniou.gestion_back.frais.FraisRepository;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@Service
public class PeriodeLocationPermissionsService extends PermissionsByRolesService {

	private final PeriodeLocationRepository periodeLocationRepository;

	public PeriodeLocationPermissionsService(AppartementRepository appartementRepository,
			UtilisateurRepository utilisateurRepository, ContactRepository contactRepository,
			FraisRepository fraisRepository, PeriodeLocationRepository periodeLocationRepository) {
		super(appartementRepository, utilisateurRepository, contactRepository, fraisRepository);
		this.periodeLocationRepository = periodeLocationRepository;
	}

	public Page<Object> obtenirPeriodesLocationsAutorises(Pageable pageable) {
		if (isUserInRole(ROLE_ADMIN)) {
			// Les administrateurs peuvent voir tous les contacts
			return periodeLocationRepository.findAll(pageable).map(this::convertToPeriodeLocationDTO);
		} else {

			if (isUserInRole(ROLE_PROPRIETAIRE)) {
				List<Object> periodeLocationDtos = new ArrayList<>();
				Set<Long> addedPeriodeLocationIds = new HashSet<>(); // Ensemble pour garder une trace des ID de contact
																		// ajoutés

				Utilisateur currentUser = getCurrentUser();
				// Les propriétaires peuvent voir les contacts de leurs appartements
				List<Long> appartementIds = currentUser.getAppartements().stream().map(Appartement::getId)
						.collect(Collectors.toList());
				periodeLocationRepository.findAllByAppartementIdIn(appartementIds, pageable)
						.forEach(periodeLocation -> {
							if (addedPeriodeLocationIds.add(periodeLocation.getId())) { // Ajoute le contact s'il n'est
																						// pas déjà ajouté
								periodeLocationDtos.add(convertToPeriodeLocationDTO(periodeLocation));
							}
						});
				return new PageImpl<>(periodeLocationDtos, pageable, periodeLocationDtos.size());

			}
			// Les gestionnaires peuvent gérer les frais associés à une période de location
			// de l'appartement qu'ils gèrent
			if (isUserInRole(ROLE_GESTIONNAIRE)) {
				Utilisateur currentUser = getCurrentUser();
				// Les gestionnaires peuvent voir les frais liés aux périodes des appartements
				// qu'ils gèrent
				List<Object> periodeLocationDtos = new ArrayList<>();

				periodeLocationRepository
						.findAllByPeriodeLocationAppartementGestionnaires(currentUser.getId(), pageable)
						.forEach(periodeLocation -> periodeLocationDtos
								.add(convertToPeriodeLocationDTO(periodeLocation)));
				return new PageImpl<>(periodeLocationDtos, pageable, periodeLocationDtos.size());
			}
			return null;
		}
	}

	public String peutGererPeriodeLocation(Long periodeLocationId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserName = authentication.getName();

		if (isUserInRole(ROLE_ADMIN)) {
			return "admin";
		}
		Optional<PeriodeLocation> periodeLocation = periodeLocationRepository.findById(periodeLocationId);
		if (periodeLocation.isEmpty()) {
			return null;
		}
		if (isUserInRole(ROLE_PROPRIETAIRE)) {
			boolean isOwner = appartementRepository.findAllByProprietaireNom(currentUserName).stream()
					.anyMatch(appartement -> appartement.getPeriodesLocations().contains(periodeLocation.get()));
			if (isOwner) {
				return "proprietaire";
			}

		}
		if (isUserInRole(ROLE_GESTIONNAIRE)) {
			Appartement appartement = periodeLocation.get().getAppartement();
			if (appartement != null && appartement.getGestionnaires().stream()
					.anyMatch(gestionnaire -> gestionnaire.getNom().equals(currentUserName))) { // Vérifie si
																								// l'utilisateur actuel
																								// est un gestionnaire
																								// de l'appartement
				return "gestionnaire";
			}
		}
		return null;
	}

	public boolean peutAjouterPeriodeLocation(PeriodeLocation periodeLocation) {
		Utilisateur currentUser = getCurrentUser();
		// Les administrateurs peuvent ajouter des contacts à n'importe quel appartement
		if (isUserInRole(ROLE_ADMIN)) {
			return true;
		}

		// Les propriétaires ne peuvent ajouter des contacts qu'à leurs propres
		// appartements
		if (isUserInRole(ROLE_PROPRIETAIRE)) {
			Long appartementId = periodeLocation.getAppartement() != null ? periodeLocation.getAppartement().getId()
					: null;
			return appartementId != null && currentUser.getAppartements().stream()
					.anyMatch(appartement -> appartement.getId().equals(appartementId));
		}
		if (isUserInRole(ROLE_GESTIONNAIRE) && periodeLocation.getAppartement() != null) {
			Appartement appartement = appartementRepository.findById(periodeLocation.getAppartement().getId()).get();
			return appartement.getGestionnaires().contains(currentUser);
		}

		// Les autres utilisateurs ne peuvent pas ajouter des contacts
		return false;
	}

	public PeriodeLocationDTO convertToPeriodeLocationDTO(PeriodeLocation periodeLocation) {
		PeriodeLocationDTO dto = new PeriodeLocationDTO();

		dto.setId(periodeLocation.getId());
		dto.setAppartementId(
				periodeLocation.getAppartement() != null ? periodeLocation.getAppartement().getId() : null);
		dto.setPrixLocation(periodeLocation.getPrixLocation());
		dto.setIsLocVac(periodeLocation.getIsLocVac());
		dto.setEstEntree(periodeLocation.getEstEntree());
		dto.setEstSortie(periodeLocation.getEstSortie());

		if (periodeLocation.getFrais() != null) {
			List<Long> fraisIds = periodeLocation.getFrais().stream().map(Frais::getId).collect(Collectors.toList());
			dto.setFraisIds(fraisIds);
		}

		return dto;
	}

}
