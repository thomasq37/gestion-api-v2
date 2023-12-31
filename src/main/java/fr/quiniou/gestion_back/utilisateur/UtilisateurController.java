package fr.quiniou.gestion_back.utilisateur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

import fr.quiniou.gestion_back.utilisateur.dto.UtilisateurDTOForAdmin;

@RestController
@RequestMapping(path = "/api/utilisateurs", produces = "application/json")
@CrossOrigin(origins = "${app.cors.origin}")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @Autowired
    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }
    
    // Obtenir tous les utilisateurs avec pagination
    @GetMapping
    public ResponseEntity<Page<Object>> obtenirTousLesUtilisateurs(Pageable pageable) {
        Page<Object> utilisateurs = utilisateurService.obtenirTousLesUtilisateurs(pageable);
        return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
    }

    // Obtenir un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenirUtilisateurParId(@PathVariable Long id) {
    	try {
    		UtilisateurDTOForAdmin utilisateur = utilisateurService.obtenirUtilisateurParId(id);
            return new ResponseEntity<>(utilisateur, HttpStatus.OK);
    	}
    	catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Ajouter un utilisateur
    @PostMapping
    public ResponseEntity<Object> ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
    	try {
    		UtilisateurDTOForAdmin utilisateurEnregistre = utilisateurService.ajouterUtilisateur(utilisateur);
            return new ResponseEntity<>(utilisateurEnregistre, HttpStatus.CREATED);
    	}
    	catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Mettre à jour un utilisateur
    @PatchMapping("/{id}")
    public ResponseEntity<Object> mettreAJourUtilisateur(@PathVariable Long id, @RequestBody Utilisateur detailsUtilisateur) {
    	try {
    		UtilisateurDTOForAdmin utilisateurMisAJour = utilisateurService.mettreAJourUtilisateur(id, detailsUtilisateur);
            return new ResponseEntity<>(utilisateurMisAJour, HttpStatus.OK);
    	}
    	catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> supprimerUtilisateur(@PathVariable Long id) {
    	try {
    		utilisateurService.supprimerUtilisateur(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    	}
    	catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
}
