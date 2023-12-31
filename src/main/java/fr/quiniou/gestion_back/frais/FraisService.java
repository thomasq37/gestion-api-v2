package fr.quiniou.gestion_back.frais;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import fr.quiniou.gestion_back.frais.dto.FraisDTOForAdminOrProprietaire;

@Service
public class FraisService {

	private final FraisRepository fraisRepository;
	private final FraisPermissionsService fraisPermissionsService;

	private static final String FRAIS_FORBIDDEN_MSG = "Vous n'avez pas les droits nécessaires pour accéder à ce(s) frais.";
	private static final String FRAIS_BADREQUEST_MSG = "Cette requête est impossible pour le moment.";
	private static final String FRAIS_NOTFOUND_MSG = "Frais introuvable.";

	public FraisService(FraisRepository fraisRepository, FraisPermissionsService fraisPermissionsService) {
		this.fraisRepository = fraisRepository;
		this.fraisPermissionsService = fraisPermissionsService;
	}

	// Obtenir tous les frais avec pagination
	public Page<Object> obtenirTousLesFrais(Pageable pageable) {
		Page<Object> frais = fraisPermissionsService.obtenirFraisAutorises(pageable);
		if(frais == null) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, FRAIS_FORBIDDEN_MSG);
		}
		return fraisPermissionsService.obtenirFraisAutorises(pageable);
	}

	// Obtenir un frais par son ID
	public Object obtenirFraisParId(Long id) {
		String isAuthorized = fraisPermissionsService.peutGererFrais(id);
		if (isAuthorized == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, FRAIS_NOTFOUND_MSG);
		} else {
			Optional<Frais> optFrais = fraisRepository.findById(id);
			if (isAuthorized == "admin" || isAuthorized == "proprietaire" || isAuthorized == "gestionnaire") {
				return fraisPermissionsService.convertToFraisDTOForAdminOrProprietaire(optFrais.get());
			}
		}
		throw new ResponseStatusException(HttpStatus.FORBIDDEN, FRAIS_FORBIDDEN_MSG);
	}

	// Ajouter un frais
	public FraisDTOForAdminOrProprietaire ajouterFrais(Frais frais) {
		if(fraisPermissionsService.peutAjouterFrais(frais) == null ) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, FRAIS_BADREQUEST_MSG);
		}
		if (!fraisPermissionsService.peutAjouterFrais(frais)) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, FRAIS_FORBIDDEN_MSG);
		}
		Frais fraisEnregistre = fraisRepository.save(frais);
		return fraisPermissionsService.convertToFraisDTOForAdminOrProprietaire(fraisEnregistre);
	}

	// Mettre à jour un frais
	public FraisDTOForAdminOrProprietaire mettreAJourFrais(Long id, Frais detailsFrais) {
		String isAuthorized = fraisPermissionsService.peutGererFrais(id);
		if (isAuthorized == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, FRAIS_NOTFOUND_MSG);
		}
		if (isAuthorized == "forbidden") {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, FRAIS_FORBIDDEN_MSG);

		}

		Frais frais = fraisRepository.findById(id).get();

		if (detailsFrais.getDebiteur() != null) {
			frais.setDebiteur(detailsFrais.getDebiteur());
		}
		if (detailsFrais.getType() != null) {
			frais.setType(detailsFrais.getType());
		}
		if (detailsFrais.getMontant() != null) {
			frais.setMontant(detailsFrais.getMontant());
		}
		if (detailsFrais.getFrequence() != null) {
			frais.setFrequence(detailsFrais.getFrequence());
		}
		fraisRepository.save(frais);
		return fraisPermissionsService.convertToFraisDTOForAdminOrProprietaire(frais);
	}

	// Supprimer un contact
	public void supprimerFrais(Long id) {
		String isAuthorized = fraisPermissionsService.peutGererFrais(id);
		if (isAuthorized == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, FRAIS_NOTFOUND_MSG);
		}
		if (isAuthorized == "forbidden") {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, FRAIS_FORBIDDEN_MSG);

		}
		fraisRepository.deleteById(id);
	}

}
