package fr.quiniou.gestion_back.contact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.quiniou.gestion_back.contact.dto.ContactDTOForOtherAdmin;


@RestController
@RequestMapping(path = "/api/contacts", produces = "application/json")
@CrossOrigin(origins = "${app.cors.origin}")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
    
    // Obtenir tous les contacts avec pagination
    @GetMapping
    public ResponseEntity<Page<Object>> obtenirTousLesContacts(Pageable pageable) {
        Page<Object> contacts = contactService.obtenirTousLesContacts(pageable);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

 // Obtenir un contact par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenirContactParId(@PathVariable Long id) {
        try {
            Object contact = contactService.obtenirContactParId(id);
            return new ResponseEntity<>(contact, HttpStatus.OK);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Ajouter un contact
    @PostMapping
    public ResponseEntity<Object> ajouterContact(@RequestBody Contact contact) {
    	try {
            Contact contactEnregistre = contactService.ajouterContact(contact);
            return new ResponseEntity<>(contactEnregistre, HttpStatus.CREATED);

    	}
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Mettre à jour un contact
    @PatchMapping("/{id}")
    public ResponseEntity<Object> mettreAJourContact(@PathVariable Long id, @RequestBody ContactDTOForOtherAdmin detailsContact) {
    	try {
    		Object contactMisAJour = contactService.mettreAJourContact(id, detailsContact);
            return new ResponseEntity<>(contactMisAJour, HttpStatus.OK);
    	}
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

 // Supprimer un contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> supprimerContact(@PathVariable Long id) {
        try {
            contactService.supprimerContact(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    
    
}
