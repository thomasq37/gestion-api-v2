package fr.quiniou.gestion_back.appartement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import fr.quiniou.gestion_back.appartement.adresse.Adresse;
import fr.quiniou.gestion_back.appartement.details.AppartementDetails;
import fr.quiniou.gestion_back.appartement.statistiques.AppartementStatistiques;

@Service
public class AppartementService {

	private final AppartementRepository appartementRepository;
	private final AppartementPermissionsService permissionsByRolesService;
	private static final String APT_FORBIDDEN_MSG = "Vous n'avez pas les droits nécessaires pour accéder à cet appartement.";
	private static final String APT_NOTFOUND_MSG = "Appartement introuvable";
	private static final String APT_ADR_NOTFOUND_MSG = "L'appartement ne possède pas d'adresse actuellement";
	private static final String APT_DETAILS_NOTFOUND_MSG = "L'appartement ne possède pas de détails actuellement";
	private static final String APT_STATS_NOTFOUND_MSG = "L'appartement ne possède pas de statistiques actuellement";

	public AppartementService(AppartementRepository appartementRepository,
			AppartementPermissionsService permissionsByRolesService) {
		this.appartementRepository = appartementRepository;
		this.permissionsByRolesService = permissionsByRolesService;
	}

	// Obtenir appartements autorisés avec DTO
	public Page<Object> obtenirTousLesAppartements(Pageable pageable) {
		return permissionsByRolesService.obtenirAppartementsAutorises(pageable);
	}

	// Obtenir un appartement par son ID si autorisé avec DTO
	public Object obtenirAppartementParId(Long id) {
		Object isAuthorizedAppartement = permissionsByRolesService.obtenirAppartementAutoriseParId(id);
		if (isAuthorizedAppartement == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		return isAuthorizedAppartement;

	}

	// Ajouter un appartement si autorisé
	public Appartement ajouterAppartement(Appartement appartement) {
		if (!permissionsByRolesService.peutGererAppartement(appartement, null, "ajouter")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		return appartementRepository.save(appartement);
	}

	// Supprimer un appartement par son id si autorisé
	public void supprimerAppartement(Long id) {
		if (!permissionsByRolesService.peutGererAppartement(null, id, "supprimer")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		appartementRepository.deleteById(id);
	}

	// ------------------------------- Section Adressse Appartement
	// ------------------------------- //

	public Adresse obtenirAdresseAppartementParAppartementId(Long appartementId) {
		if (!permissionsByRolesService.peutGererAppartement(null, appartementId, "obtenir")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(appartementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));

		if (appartement.getAdresse() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, APT_ADR_NOTFOUND_MSG);
		}

		return appartement.getAdresse();
	}

	// Mettre a jour adresse appartement par id
	public Adresse majAdresseAppartementParAppartementId(Long id, Adresse nouvelleAdresse) {
		if (!permissionsByRolesService.peutGererAppartement(null, id, "maj")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));
		appartement.setAdresse(nouvelleAdresse);
		Appartement appartementEnregistre = appartementRepository.save(appartement);
		return appartementEnregistre.getAdresse();
	}

	// Supprimer (ou effacer) l'adresse d'un appartement
	public void supprimerAdresseParAppartementId(Long id) {
		if (!permissionsByRolesService.peutGererAppartement(null, id, "maj")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));

		if (appartement.getAdresse() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, APT_ADR_NOTFOUND_MSG);
		}

		appartement.setAdresse(null);
		appartementRepository.save(appartement);
	}

	// ------------------------------- Section Details Appartement
	// ------------------------------- //

	public AppartementDetails obtenirDetailsAppartementParAppartementId(Long appartementId) {
		if (!permissionsByRolesService.peutGererAppartement(null, appartementId, "obtenir")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(appartementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));

		if (appartement.getDetails() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, APT_DETAILS_NOTFOUND_MSG);
		}

		return appartement.getDetails();
	}

	// Mettre a jour adresse appartement par id
	public AppartementDetails majDetailsAppartementParAppartementId(Long id, AppartementDetails nouveauDetails) {
		if (!permissionsByRolesService.peutGererAppartement(null, id, "maj")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));
		appartement.setDetails(nouveauDetails);
		Appartement appartementEnregistre = appartementRepository.save(appartement);
		return appartementEnregistre.getDetails();
	}

	// Supprimer (ou effacer) les details d'un appartement
	public void supprimerDetailsParAppartementId(Long id) {
		if (!permissionsByRolesService.peutGererAppartement(null, id, "maj")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));

		if (appartement.getDetails() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, APT_DETAILS_NOTFOUND_MSG);
		}

		appartement.setDetails(null);
		appartementRepository.save(appartement);
	}

	// ------------------------------- Section Statistiques Appartement
	// ------------------------------- //
	
	public AppartementStatistiques obtenirStatistiquesParAppartementId(Long appartementId) {
		if (!permissionsByRolesService.peutGererAppartement(null, appartementId, "obtenir")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(appartementId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));

		if (appartement.getDetails() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, APT_STATS_NOTFOUND_MSG);
		}

		return appartement.getStatistiques();
	}

	public AppartementStatistiques majStatistiquesAppartementParAppartementId(Long id,
			AppartementStatistiques nouveauStatistiques) {
		if (!permissionsByRolesService.peutGererAppartement(null, id, "maj")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));
		appartement.setStatistiques(nouveauStatistiques);
		Appartement appartementEnregistre = appartementRepository.save(appartement);
		return appartementEnregistre.getStatistiques();
	}

	public void supprimerStatistiquesParAppartementId(Long id) {
		if (!permissionsByRolesService.peutGererAppartement(null, id, "maj")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, APT_FORBIDDEN_MSG);
		}
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, APT_NOTFOUND_MSG));

		if (appartement.getStatistiques() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, APT_STATS_NOTFOUND_MSG);
		}

		appartement.setStatistiques(null);
		appartementRepository.save(appartement);
		
	}

}