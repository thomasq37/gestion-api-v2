package fr.quiniou.gestion_back.invitation;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class InvitationService {
	
	private final InvitationRepository invitationRepository;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }
    
    // Obtenir toutes les invitations avec pagination
    public Page<Invitation> obtenirToutesLesInvitations(Pageable pageable) {
        return invitationRepository.findAll(pageable);
    }

    // Obtenir une invitation par son ID
    public Optional<Invitation> obtenirInvitationParId(Long id) {
        return invitationRepository.findById(id);
    }

    // Ajouter une invitation
    public Invitation ajouterInvitation(Invitation invitation) {
        return invitationRepository.save(invitation);
    }

    // Mettre à jour une invitation
    public Invitation mettreAJourInvitation(Long id, Invitation detailsInvitation) {
        // Vérifier si l'invitation existe, puis mettre à jour
    	Invitation invitation = invitationRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Invitation avec ID " + id + " non trouvé"));

    	// Mettre à jour les attributs de l'invitation
    	if (detailsInvitation.getMdp() != null) {
    	    invitation.setMdp(detailsInvitation.getMdp());
    	}

    	if (detailsInvitation.getActive() != null) {
    	    invitation.setActive(detailsInvitation.getActive());
    	}

        return invitationRepository.save(invitation);
    }

    // Supprimer une invitation
    public void supprimerInvitation(Long id) {
        // Vérifier si l'invitation existe, puis la supprimer
        boolean exists = invitationRepository.existsById(id);
        if (!exists) {
            throw new IllegalStateException("Invitation avec ID " + id + " non trouvé");
        }

        invitationRepository.deleteById(id);
    }

	

}
