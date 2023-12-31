package fr.quiniou.gestion_back.frais;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fr.quiniou.gestion_back.appartement.Appartement;
import fr.quiniou.gestion_back.appartement.AppartementRepository;
import fr.quiniou.gestion_back.auth.PermissionsByRolesService;
import fr.quiniou.gestion_back.contact.ContactRepository;
import fr.quiniou.gestion_back.frais.dto.FraisDTOForAdminOrProprietaire;
import fr.quiniou.gestion_back.periode_location.PeriodeLocation;
import fr.quiniou.gestion_back.periode_location.PeriodeLocationRepository;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@Service
public class FraisPermissionsService extends PermissionsByRolesService{
	private final PeriodeLocationRepository periodeLocationRepository;
	public FraisPermissionsService(
			AppartementRepository appartementRepository,
			UtilisateurRepository utilisateurRepository, 
			ContactRepository contactRepository,
			FraisRepository fraisRepository,
			PeriodeLocationRepository periodeLocationRepository) {
		super(appartementRepository, utilisateurRepository, contactRepository, fraisRepository);
		this.periodeLocationRepository = periodeLocationRepository;
	}

	public String peutGererFrais(Long id) {
		Optional<Frais> optFrais = fraisRepository.findById(id);
	    if (optFrais.isEmpty()) {
	        return null;
	    } else {
	        Utilisateur currentUser = getCurrentUser();
	        // Les administrateurs peuvent gérer tous les frais
	        if (isUserInRole(ROLE_ADMIN)) {
	            return "admin";
	        }

	        Frais frais = optFrais.get();

	        // Les propriétaires peuvent gérer les frais de leurs appartements
	        if (isUserInRole(ROLE_PROPRIETAIRE)) {
	            Long appartementId = frais.getAppartement() != null ? frais.getAppartement().getId() : null;
	            Boolean isProprietaire = appartementId != null && currentUser.getAppartements().stream()
	                    .anyMatch(appartement -> appartement.getId().equals(appartementId));
	            if (isProprietaire) {
	                return "proprietaire";
	            }
	        }

	        // Les gestionnaires peuvent gérer les frais associés à une période de location de l'appartement qu'ils gèrent
	        if (isUserInRole(ROLE_GESTIONNAIRE) && frais.getPeriodeLocation() != null) {
	            Appartement appartement = frais.getPeriodeLocation().getAppartement();
	            if (appartement != null && appartement.getGestionnaires().contains(currentUser)) {
	                return "gestionnaire";
	            }
	        }
	    }
	    // Les autres utilisateurs ne peuvent pas gérer les frais
	    return "forbidden";
	}
	
	public Boolean peutAjouterFrais(Frais frais) {
		Utilisateur currentUser = getCurrentUser();
		
		if(frais.getAppartement() != null && frais.getPeriodeLocation() != null) {
			return null;
		}
		// Les administrateurs peuvent ajouter des frais à n'importe quel appartement ou periode
		if (isUserInRole(ROLE_ADMIN)) {
			return true;
		}

		// Les propriétaires ne peuvent ajouter des frais qu'à leurs propres
		// appartements
		if (isUserInRole(ROLE_PROPRIETAIRE)) {
			Long appartementId = frais.getAppartement() != null ? frais.getAppartement().getId() : null;
			return appartementId != null && currentUser.getAppartements().stream()
					.anyMatch(appartement -> appartement.getId().equals(appartementId));
		}
		if (isUserInRole(ROLE_GESTIONNAIRE) && frais.getPeriodeLocation() != null) {
			PeriodeLocation location = periodeLocationRepository.findById(frais.getPeriodeLocation().getId()).get();
            Appartement appartement = location.getAppartement();
            if (appartement != null && appartement.getGestionnaires().contains(currentUser)) {
                return true;
            }
        }
		return false;
	}

    public FraisDTOForAdminOrProprietaire convertToFraisDTOForAdminOrProprietaire(Frais frais) {
    	FraisDTOForAdminOrProprietaire dto = new FraisDTOForAdminOrProprietaire();
    	dto.setId(frais.getId());
        dto.setType(frais.getType());
        dto.setMontant(frais.getMontant());
        dto.setDebiteur(frais.getDebiteur());

        if (frais.getAppartement() != null) {
            dto.setAppartementId(frais.getAppartement().getId());
        }
        if (frais.getPeriodeLocation() != null) {
            dto.setPeriodeLocationId(frais.getPeriodeLocation().getId());
        }
        return dto;
    }

	
    public Page<Object> obtenirFraisAutorises(Pageable pageable) {
    	if (isUserInRole(ROLE_ADMIN)) {
	        // Les administrateurs peuvent voir tous les contacts
	        return fraisRepository.findAll(pageable).map(this::convertToFraisDTOForAdminOrProprietaire);
	    }
    	if (isUserInRole(ROLE_PROPRIETAIRE)) {
    	    Utilisateur currentUser = getCurrentUser();
            // Les propriétaires peuvent voir les contacts de leurs appartements
            List<Long> appartementIds = currentUser.getAppartements().stream().map(Appartement::getId)
                    .collect(Collectors.toList());
            List<Object> fraisDtos = new ArrayList<>();
	        Set<Long> addedFraisIds = new HashSet<>(); // Ensemble pour garder une trace des ID de contact ajoutés

            
            fraisRepository.findAllByAppartementIdIn(appartementIds, pageable)
                    .forEach(frais -> {
                        if (addedFraisIds.add(frais.getId())) { // Ajoute le contact s'il n'est pas déjà ajouté
                            fraisDtos.add(convertToFraisDTOForAdminOrProprietaire(frais));
                        }
                    });
	        return new PageImpl<>(fraisDtos, pageable, fraisDtos.size());

        }
    	if (isUserInRole(ROLE_GESTIONNAIRE)) {
    		Utilisateur currentUser = getCurrentUser();
            // Les gestionnaires peuvent voir les frais liés aux périodes des appartements qu'ils gèrent
            List<Object> fraisDtos = new ArrayList<>();

            fraisRepository.findAllByPeriodeLocationAppartementGestionnaires(currentUser, pageable)
                    .forEach(frais -> fraisDtos.add(convertToFraisDTOForAdminOrProprietaire(frais)));
            return new PageImpl<>(fraisDtos, pageable, fraisDtos.size());
    	}
    	return null;
	}

}
