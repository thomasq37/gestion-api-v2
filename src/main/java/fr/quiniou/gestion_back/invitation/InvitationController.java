package fr.quiniou.gestion_back.invitation;

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
@RequestMapping(path = "/api/invitations", produces = "application/json")
@CrossOrigin(origins = "${app.cors.origin}")
public class InvitationController {
	private final InvitationService invitationService;

    @Autowired
    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }
    
    // Obtenir toutes les invitation avec pagination
    @GetMapping
    public ResponseEntity<Page<Invitation>> obtenirToutesLesInvitations(Pageable pageable) {
        Page<Invitation> invitations = invitationService.obtenirToutesLesInvitations(pageable);
        return new ResponseEntity<>(invitations, HttpStatus.OK);
    }

    // Obtenir une invitation par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Invitation> obtenirInvitationParId(@PathVariable Long id) {
    	Invitation invitation = invitationService.obtenirInvitationParId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Invitation non trouvé"));
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    // Ajouter une invitation
    @PostMapping
    public ResponseEntity<Invitation> ajouterInvitation(@RequestBody Invitation invitation) {
    	Invitation invitationEnregistre = invitationService.ajouterInvitation(invitation);
        return new ResponseEntity<>(invitationEnregistre, HttpStatus.CREATED);
    }

    // Mettre à jour une invitation
    @PatchMapping("/{id}")
    public ResponseEntity<Invitation> mettreAJourInvitation(@PathVariable Long id, @RequestBody Invitation detailsInvitation) {
    	Invitation invitationMisAJour = invitationService.mettreAJourInvitation(id, detailsInvitation);
        return new ResponseEntity<>(invitationMisAJour, HttpStatus.OK);
    }

    // Supprimer une invitation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerInvitation(@PathVariable Long id) {
    	invitationService.supprimerInvitation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
