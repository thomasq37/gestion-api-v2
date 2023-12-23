package fr.quiniou.gestion_back.utilisateur;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UtilisateurService {
	private final UtilisateurRepository utilisateurRepository;
	
	@Autowired
	private UtilisateurPermissionsService customPermissionsService;
	
	private final PasswordEncoder passwordEncoder;

    @Autowired
    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    // Obtenir tous les utilisateurs avec pagination autorisés
    public Page<UtilisateurDTO> obtenirTousLesUtilisateurs(Pageable pageable) {
        return customPermissionsService.obtenirUtilisateursAutorises(pageable);
    }
    public Optional<UtilisateurDTO> obtenirUtilisateurParId(Long id) {
        if (!customPermissionsService.peutGererUtilisateur(id, "obtenir")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return utilisateurRepository.findById(id)
                .map(customPermissionsService::convertToUtilisateurDTO); // Utilisation de la méthode de conversion du service
    }
    
    // Obtenir tous les utilisateurs avec pagination admin
    public Page<Utilisateur> obtenirTousLesUtilisateursAdmin(Pageable pageable) {
    	if (!customPermissionsService.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return customPermissionsService.obtenirUtilisateurs(pageable);
    }
    public Optional<Utilisateur> obtenirUtilisateurParIdAdmin(Long id) {
        if (!customPermissionsService.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        return utilisateurRepository.findById(id);
    }


    // Mettre a jour utilisateur si autorisé
    public Utilisateur mettreAJourUtilisateur(Long id, Utilisateur detailsUtilisateur) {
        if (!customPermissionsService.peutGererUtilisateur(id, "mettreAJour")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        // Vérifier si l'utilisateur existe, puis mettre à jour
    	Utilisateur utilisateur = utilisateurRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Utilisateur avec ID " + id + " non trouvé"));

    	// Mettre à jour les champs non nuls
        if (detailsUtilisateur.getNom() != null) utilisateur.setNom(detailsUtilisateur.getNom());
        if (detailsUtilisateur.getEmail() != null) utilisateur.setEmail(detailsUtilisateur.getEmail());
        if (detailsUtilisateur.getTelNumero() != null) utilisateur.setTelNumero(detailsUtilisateur.getTelNumero());
        if (detailsUtilisateur.getMdp() != null) utilisateur.setMdp(passwordEncoder.encode(detailsUtilisateur.getMdp()));


        return utilisateurRepository.save(utilisateur);
    }
    
    
    // Supprimer utilisateur si autorisé
    public void supprimerUtilisateur(Long id) {
        if (!customPermissionsService.peutGererUtilisateur(id, "supprimer")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }
        utilisateurRepository.deleteById(id);
    }

    // Ajouter un utilisateur
    public Utilisateur ajouterUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }
}
