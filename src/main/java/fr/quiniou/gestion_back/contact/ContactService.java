package fr.quiniou.gestion_back.contact;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContactService {
	private final ContactRepository contactRepository;
	
	@Autowired
	private ContactPermissionsService customPermissionsService;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }
    
    // Obtenir tous les contacts avec pagination
    public Page<Contact> obtenirTousLesContacts(Pageable pageable) {
        return customPermissionsService.obtenirContactsAutorises(pageable);
    }

    // Obtenir un contact par son ID
    public Optional<Contact> obtenirContactParId(Long id) {
    	if (!customPermissionsService.peutGererContact(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return contactRepository.findById(id);
    }

    // Ajouter un contact
    public Contact ajouterContact(Contact contact) {
    	if (!customPermissionsService.peutAjouterContact(contact)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return contactRepository.save(contact);
    }

    // Mettre à jour un contact
    public Contact mettreAJourContact(Long id, Contact detailsContact) {
        // Vérifier si le contact existe, puis mettre à jour
        Contact contact = contactRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Contact avec ID " + id + " non trouvé"));

        // Mettre à jour les attributs du contact
        contact.setNom(detailsContact.getNom());
        contact.setEmail(detailsContact.getEmail());
        contact.setTelNumero(detailsContact.getTelNumero());

        return contactRepository.save(contact);
    }

    // Supprimer un contact
    public void supprimerContact(Long id) {
    	if (!customPermissionsService.peutGererContact(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        // Vérifier si le contact existe, puis le supprimer
        boolean exists = contactRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("Contact avec ID " + id + " non trouvé");
        }

        contactRepository.deleteById(id);
    }
  
	
}
