package fr.quiniou.gestion_back.contact;

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
import fr.quiniou.gestion_back.contact.dto.ContactDTOForAdmin;
import fr.quiniou.gestion_back.contact.dto.ContactDTOForOtherAdmin;
import fr.quiniou.gestion_back.frais.FraisRepository;
import fr.quiniou.gestion_back.utilisateur.Utilisateur;
import fr.quiniou.gestion_back.utilisateur.UtilisateurRepository;

@Service
public class ContactPermissionsService extends PermissionsByRolesService {

	public ContactPermissionsService(
			AppartementRepository appartementRepository,
			UtilisateurRepository utilisateurRepository, 
			ContactRepository contactRepository,
			FraisRepository fraisRepository) {
		super(appartementRepository, utilisateurRepository, contactRepository, fraisRepository);
	}

	public boolean peutAjouterContact(Contact contact) {
		Utilisateur currentUser = getCurrentUser();
		// Les administrateurs peuvent ajouter des contacts à n'importe quel appartement
		if (isUserInRole(ROLE_ADMIN)) {
			return true;
		}

		// Les propriétaires ne peuvent ajouter des contacts qu'à leurs propres
		// appartements
		if (isUserInRole(ROLE_PROPRIETAIRE)) {
			Long appartementId = contact.getAppartement() != null ? contact.getAppartement().getId() : null;
			return appartementId != null && currentUser.getAppartements().stream()
					.anyMatch(appartement -> appartement.getId().equals(appartementId));
		}

		// Les autres utilisateurs ne peuvent pas ajouter des contacts
		return false;
	}

	public String peutGererContact(Long contactId) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String currentUserName = authentication.getName();

	    if (isUserInRole(ROLE_ADMIN)) {
	        return "admin";
	    }

	    if (isUserInRole(ROLE_PROPRIETAIRE)) {
	        Optional<Contact> contact = contactRepository.findById(contactId);
	        if (contact.isPresent()) {
	            boolean isOwner = appartementRepository.findAllByProprietaireNom(currentUserName).stream()
	                    .anyMatch(appartement -> appartement.getContacts().contains(contact.get()));
	            if (isOwner) {
	                return "proprietaire";
	            }
	        }
	    }
	    if (isUserInRole(ROLE_GESTIONNAIRE) || isUserInRole(ROLE_VIEWER)) {
	        Optional<Contact> contact = contactRepository.findById(contactId);
	        if (contact.isPresent()) {
				boolean isPublic = appartementRepository.findByPublicAppartementTrue().stream()
	                    .anyMatch(appartement -> appartement.getContacts().contains(contact.get()));
	            if (isPublic) {
	                return "public"; 
	            }
	        }
	    }
	    return null;
	}
	

	public Page<Object> obtenirContactsAutorises(Pageable pageable) {
	    if (isUserInRole(ROLE_ADMIN)) {
	        // Les administrateurs peuvent voir tous les contacts
	        return contactRepository.findAll(pageable).map(this::convertToContactDTOForAdmin);
	    } else {
	        List<Object> contactsDtos = new ArrayList<>();
	        Set<Long> addedContactIds = new HashSet<>(); // Ensemble pour garder une trace des ID de contact ajoutés

	        if (isUserInRole(ROLE_PROPRIETAIRE)) {
	    	    Utilisateur currentUser = getCurrentUser();
	            // Les propriétaires peuvent voir les contacts de leurs appartements
	            List<Long> appartementIds = currentUser.getAppartements().stream().map(Appartement::getId)
	                    .collect(Collectors.toList());
	            contactRepository.findAllByAppartementIdIn(appartementIds, pageable)
	                    .forEach(contact -> {
	                        if (addedContactIds.add(contact.getId())) { // Ajoute le contact s'il n'est pas déjà ajouté
	                            contactsDtos.add(convertToContactDTOForOtherAdmin(contact));
	                        }
	                    });
	        }
	        
	        List<Appartement> publicAppartements = appartementRepository.findByPublicAppartementTrue();
	        for (Appartement appartement : publicAppartements) {
	            for (Contact contact : appartement.getContacts()) {
	                if (addedContactIds.add(contact.getId())) { // Vérifie si l'ID du contact n'est pas déjà ajouté
	                    contactsDtos.add(convertToContactDTOForOtherAdmin(contact));
	                }
	            }
	        }
	        return new PageImpl<>(contactsDtos, pageable, contactsDtos.size());
	    }
	}

	public ContactDTOForAdmin convertToContactDTOForAdmin(Contact contact) {
		ContactDTOForAdmin dto = new ContactDTOForAdmin();

		dto.setId(contact.getId());
		dto.setNom(contact.getNom());
		dto.setEmail(contact.getEmail());
		dto.setTelNumero(contact.getTelNumero());
		dto.setAppartementId(contact.getAppartement().getId());

		return dto;
	}

	public ContactDTOForOtherAdmin convertToContactDTOForOtherAdmin(Contact contact) {
		ContactDTOForOtherAdmin dto = new ContactDTOForOtherAdmin();

		dto.setNom(contact.getNom());
		dto.setEmail(contact.getEmail());
		dto.setTelNumero(contact.getTelNumero());
		dto.setAppartementId(contact.getAppartement().getId());

		return dto;
	}

}
