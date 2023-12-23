package fr.quiniou.gestion_back.appartement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import fr.quiniou.gestion_back.utilisateur.UtilisateurPermissionsService;

@Service
public class AppartementService {

	@Autowired
	private AppartementRepository appartementRepository;

	@Autowired
	private AppartementPermissionsService customPermissionsService;

	@Autowired
	private UtilisateurPermissionsService utilisateurPermissionsService;

	// Obtenir appartements autorisés avec DTO
	public Page<AppartementDTO> obtenirTousLesAppartements(Pageable pageable) {
		return customPermissionsService.obtenirAppartementsAutorises(pageable);
	}

	// Obtenir un appartement par son ID si autorisé avec DTO
	public AppartementDTO obtenirAppartementParId(Long id) {
		return customPermissionsService.obtenirAppartementAutoriseParId(id);

	}

	// Ajouter un appartement si autorisé
	public Appartement ajouterAppartement(Appartement appartement) {
		if (!customPermissionsService.peutGererAppartement(appartement, null, "ajouter")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
		return appartementRepository.save(appartement);
	}

	// Supprimer un appartement par son id si autorisé
	public void supprimerAppartement(Long id) {
		if (!customPermissionsService.peutGererAppartement(null, id, "supprimer")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
		appartementRepository.deleteById(id);
	}

	
	// ------------------------------- Section Adressse Appartement ------------------------------- //

	
	public Adresse obtenirAdresseAppartementParAppartementId(Long appartementId) {
		if (!customPermissionsService.peutGererAppartement(null, appartementId, "obtenir")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
		Appartement appartement = appartementRepository.findById(appartementId)
				.orElseThrow(() -> new RuntimeException("Appartement non trouvé"));

		if(appartement.getAdresse() == null) {
			throw new RuntimeException("L'appartement ne possède pas d'adresse actuellement");
		}

		return appartement.getAdresse();
	}

	// Mettre a jour adresse appartement par id
	public Adresse majAdresseAppartementParAppartementId(Long id, Adresse nouvelleAdresse) {
		if (!customPermissionsService.peutGererAppartement(null, id, "maj")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Appartement non trouvé"));
		appartement.setAdresse(nouvelleAdresse);
		Appartement appartementEnregistre = appartementRepository.save(appartement);
		return appartementEnregistre.getAdresse();
	}

	// Supprimer (ou effacer) l'adresse d'un appartement
	public void supprimerAdresseParAppartementId(Long id) {
		if (!customPermissionsService.peutGererAppartement(null, id, "maj")) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
		}
		Appartement appartement = appartementRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Appartement non trouvé"));
		
		if(appartement.getAdresse() == null) {
			throw new RuntimeException("L'appartement ne possède pas d'adresse actuellement");
		}

		appartement.setAdresse(null); // Créer une nouvelle adresse vide
		appartementRepository.save(appartement);
	}

}