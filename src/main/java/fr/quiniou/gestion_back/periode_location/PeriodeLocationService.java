package fr.quiniou.gestion_back.periode_location;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PeriodeLocationService {
	
	private final PeriodeLocationRepository periodeLocationRepository;
	private final PeriodeLocationPermissionsService periodeLocationPermissionsService;
	
	private static final String PERIODELOC_FORBIDDEN_MSG = "Vous n'avez pas les droits nécessaires pour accéder à cette période de location.";
	private static final String PERIODELOC_BADREQUEST_MSG = "Cette requête est impossible pour le moment.";
	private static final String PERIODELOC_NOTFOUND_MSG = "Période de location introuvable.";

	
	public PeriodeLocationService(
			PeriodeLocationRepository periodeLocationRepository,
			PeriodeLocationPermissionsService periodeLocationPermissionsService) {
        this.periodeLocationRepository = periodeLocationRepository;
        this.periodeLocationPermissionsService = periodeLocationPermissionsService;
    }
	
	public Page<Object> obtenirTousLesPeriodesLocations(Pageable pageable) {
		if(periodeLocationPermissionsService.obtenirPeriodesLocationsAutorises(pageable) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  PERIODELOC_FORBIDDEN_MSG);
		}
        return periodeLocationPermissionsService.obtenirPeriodesLocationsAutorises(pageable);
	}

	public Object obtenirPeriodeLocationParId(Long id) {
		Optional<PeriodeLocation> optPeriodeLocation = periodeLocationRepository.findById(id);
    	String isAuthorized = periodeLocationPermissionsService.peutGererPeriodeLocation(id);
    	if (isAuthorized == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  PERIODELOC_FORBIDDEN_MSG);
        }
    	
    	if(optPeriodeLocation.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, PERIODELOC_NOTFOUND_MSG);
    	}
    	if(isAuthorized != "admin" || isAuthorized == "proprietaire" || isAuthorized == "gestionnaire") {
    		return periodeLocationPermissionsService.convertToPeriodeLocationDTO(optPeriodeLocation.get());
    	}
    	else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur est survenue.");
    	}
	}

	public PeriodeLocationDTO ajouterPeriodeLocation(PeriodeLocation periodeLocation) {
		if (!periodeLocationPermissionsService.peutAjouterPeriodeLocation(periodeLocation)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  PERIODELOC_FORBIDDEN_MSG);
        }
        periodeLocationRepository.save(periodeLocation);
        return periodeLocationPermissionsService.convertToPeriodeLocationDTO(periodeLocation);

	}

	public Object mettreAJourPeriodeLocation(Long id, PeriodeLocationDTO detailsPeriodeLocation) {
		String isAuthorized = periodeLocationPermissionsService.peutGererPeriodeLocation(id);
    	if (isAuthorized == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, PERIODELOC_FORBIDDEN_MSG);
        }
        // Vérifier si la periodeLocation existe, puis mettre à jour
        PeriodeLocation periodeLocation = periodeLocationRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, PERIODELOC_NOTFOUND_MSG));
        if (detailsPeriodeLocation.getAppartementId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PERIODELOC_BADREQUEST_MSG);
        }
        // Mettre à jour les attributs du contact
        if(detailsPeriodeLocation.getEstEntree() != null) {
            periodeLocation.setEstEntree(detailsPeriodeLocation.getEstEntree());

        }
        if(detailsPeriodeLocation.getEstSortie() != null) {
            periodeLocation.setEstSortie(detailsPeriodeLocation.getEstSortie());

        }
        if(detailsPeriodeLocation.getPrixLocation() != null) {
            periodeLocation.setPrixLocation(detailsPeriodeLocation.getPrixLocation());

        }
        if(detailsPeriodeLocation.getIsLocVac() != null) {
            periodeLocation.setIsLocVac(detailsPeriodeLocation.getIsLocVac());

        }
    
        periodeLocationRepository.save(periodeLocation);
        return periodeLocationPermissionsService.convertToPeriodeLocationDTO(periodeLocation);
	}

	public void supprimerPeriodeLocation(Long id) {
		if (periodeLocationPermissionsService.peutGererPeriodeLocation(id) == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, PERIODELOC_FORBIDDEN_MSG);
        }
        // Vérifier si la période de location existe, puis la supprimer
        boolean exists = periodeLocationRepository.existsById(id);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, PERIODELOC_NOTFOUND_MSG);
        }
        periodeLocationRepository.deleteById(id);
		
	}

}
