package fr.quiniou.gestion_back.contact;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import fr.quiniou.gestion_back.auth.PermissionsByRolesService;
import fr.quiniou.gestion_back.contact.dto.ContactDTOForOtherAdmin;

@Service
public class ContactService {
	
	private final ContactRepository contactRepository;
	private final ContactPermissionsService contactPermissionsService;
	
	private static final String CONTACT_FORBIDDEN_MSG = "Vous n'avez pas les droits nécessaires pour accéder à ce contact.";
	private static final String CONTACT_BADREQUEST_MSG = "Cette requête est impossible pour le moment.";
	private static final String CONTACT_NOTFOUND_MSG = "Contact introuvable.";

	
    public ContactService(
    		ContactRepository contactRepository,
    		ContactPermissionsService contactPermissionsService) {
        this.contactRepository = contactRepository;
        this.contactPermissionsService = contactPermissionsService;
    }
    
    // Obtenir tous les contacts avec pagination
    public Page<Object> obtenirTousLesContacts(Pageable pageable) {
        return contactPermissionsService.obtenirContactsAutorises(pageable);
    }

    // Obtenir un contact par son ID
    public Object obtenirContactParId(Long id) {
    	Optional<Contact> optContact = contactRepository.findById(id);
    	String isAuthorized = contactPermissionsService.peutGererContact(id);
    	if (isAuthorized == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  CONTACT_FORBIDDEN_MSG);
        }
    	
    	if(optContact.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONTACT_NOTFOUND_MSG);
    	}
    	if(isAuthorized == "admin") {
    		return contactPermissionsService.convertToContactDTOForAdmin(optContact.get());
    	}
    	else if(isAuthorized == "proprietaire" || isAuthorized == "public") {
    		
    		return contactPermissionsService.convertToContactDTOForOtherAdmin(optContact.get());
    	}
    	else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur est survenue.");
    	}
    }

    // Ajouter un contact
    public Contact ajouterContact(Contact contact) {
    	if (!contactPermissionsService.peutAjouterContact(contact)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,  CONTACT_FORBIDDEN_MSG);
        }
        return contactRepository.save(contact);
    }

    // Mettre à jour un contact
    public Object mettreAJourContact(Long id, ContactDTOForOtherAdmin detailsContact) {
    	String isAuthorized = contactPermissionsService.peutGererContact(id);
    	if (isAuthorized == null || isAuthorized == "public") {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, CONTACT_FORBIDDEN_MSG);
        }
        // Vérifier si le contact existe, puis mettre à jour
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, CONTACT_NOTFOUND_MSG));
        if (detailsContact.getAppartementId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, CONTACT_BADREQUEST_MSG);
        }
        // Mettre à jour les attributs du contact
        contact.setNom(detailsContact.getNom());
        contact.setEmail(detailsContact.getEmail());
        contact.setTelNumero(detailsContact.getTelNumero());
        contactRepository.save(contact);
        if(isAuthorized == "admin") {
        	return contactPermissionsService.convertToContactDTOForAdmin(contact);
        }
        
        return contactPermissionsService.convertToContactDTOForOtherAdmin(contact);
    }

    // Supprimer un contact
    public void supprimerContact(Long id) {
    	if (contactPermissionsService.peutGererContact(id) == null || contactPermissionsService.peutGererContact(id) == "public") {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, CONTACT_FORBIDDEN_MSG);
        }
        // Vérifier si le contact existe, puis le supprimer
        boolean exists = contactRepository.existsById(id);
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, CONTACT_NOTFOUND_MSG);
        }
		contactRepository.deleteById(id);
    }
  
	
}
