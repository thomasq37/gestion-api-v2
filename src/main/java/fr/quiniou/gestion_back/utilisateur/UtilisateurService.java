package fr.quiniou.gestion_back.utilisateur;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import fr.quiniou.gestion_back.auth.PermissionsByRolesService;
import fr.quiniou.gestion_back.utilisateur.dto.UtilisateurDTOForAdmin;

@Service
public class UtilisateurService {
	private final UtilisateurRepository utilisateurRepository;
	private final UtilisateurPermissionsService utilisateursPermissionsByRolesService;
	private final PasswordEncoder passwordEncoder;
	private static final String USER_FORBIDDEN_MSG = "Vous n'avez pas les droits nécessaires pour accéder à cet(te) utilisateur(trice).";
	private static final String USER_NOTFOUND_MSG = "Utilisateur(trice) introuvable.";


    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder, UtilisateurPermissionsService utilisateursPermissionsByRolesService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.utilisateursPermissionsByRolesService = utilisateursPermissionsByRolesService;
    }
    
    // Obtenir tous les utilisateurs avec pagination autorisés
    public Page<Object> obtenirTousLesUtilisateurs(Pageable pageable) {
        return utilisateursPermissionsByRolesService.obtenirUtilisateursAutorises(pageable);
    }
    
    public UtilisateurDTOForAdmin obtenirUtilisateurParId(Long id) {
        if (!utilisateursPermissionsByRolesService.peutGererUtilisateur(id, "obtenir")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, USER_FORBIDDEN_MSG);
        }
        Optional<Utilisateur> optUtilisateur  = utilisateurRepository.findById(id);
    	if(optUtilisateur.isEmpty()) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOTFOUND_MSG);
    	}
        return utilisateursPermissionsByRolesService.convertToUtilisateurDTOForAdmin(optUtilisateur.get());
    }


    // Mettre a jour utilisateur si autorisé
    public UtilisateurDTOForAdmin mettreAJourUtilisateur(Long id, Utilisateur detailsUtilisateur) {
        if (!utilisateursPermissionsByRolesService.peutGererUtilisateur(id, "maj")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, USER_FORBIDDEN_MSG);
        }
        // Vérifier si l'utilisateur existe, puis mettre à jour
    	Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOTFOUND_MSG));

    	// Mettre à jour les champs non nuls
        if (detailsUtilisateur.getNom() != null) utilisateur.setNom(detailsUtilisateur.getNom());
        if (detailsUtilisateur.getEmail() != null) utilisateur.setEmail(detailsUtilisateur.getEmail());
        if (detailsUtilisateur.getTelNumero() != null) utilisateur.setTelNumero(detailsUtilisateur.getTelNumero());
        if (detailsUtilisateur.getMdp() != null) utilisateur.setMdp(passwordEncoder.encode(detailsUtilisateur.getMdp()));


        Utilisateur utilisateurEnregistre = utilisateurRepository.save(utilisateur);
        return utilisateursPermissionsByRolesService.convertToUtilisateurDTOForAdmin(utilisateurEnregistre);
    }
    
    
    // Supprimer utilisateur si autorisé
    public void supprimerUtilisateur(Long id) {
        if (!utilisateursPermissionsByRolesService.peutGererUtilisateur(id, "supprimer")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, USER_FORBIDDEN_MSG);
        }
        utilisateurRepository.deleteById(id);
    }

    // Ajouter un utilisateur
    public UtilisateurDTOForAdmin ajouterUtilisateur(Utilisateur utilisateur) {
    	Utilisateur utilisateurEnregistre = utilisateurRepository.save(utilisateur);
        return utilisateursPermissionsByRolesService.convertToUtilisateurDTOForAdmin(utilisateurEnregistre);
    }
}
