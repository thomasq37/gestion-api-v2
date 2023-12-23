package fr.quiniou.gestion_back.contact;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping(path = "/api/contacts", produces = "application/json")
@CrossOrigin(origins = "${app.cors.origin}")
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
    
    // Obtenir tous les contacts avec pagination
    @GetMapping
    public ResponseEntity<Page<Contact>> obtenirTousLesContacts(Pageable pageable) {
        Page<Contact> contacts = contactService.obtenirTousLesContacts(pageable);
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    // Obtenir un contact par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Contact> obtenirContactParId(@PathVariable Long id) {
        Contact contact = contactService.obtenirContactParId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact non trouvé"));
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    // Ajouter un contact
    @PostMapping
    public ResponseEntity<Contact> ajouterContact(@RequestBody Contact contact) {
        Contact contactEnregistre = contactService.ajouterContact(contact);
        return new ResponseEntity<>(contactEnregistre, HttpStatus.CREATED);
    }

    // Mettre à jour un contact
    @PatchMapping("/{id}")
    public ResponseEntity<Contact> mettreAJourContact(@PathVariable Long id, @RequestBody Contact detailsContact) {
        Contact contactMisAJour = contactService.mettreAJourContact(id, detailsContact);
        return new ResponseEntity<>(contactMisAJour, HttpStatus.OK);
    }

    // Supprimer un contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerContact(@PathVariable Long id) {
        contactService.supprimerContact(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
