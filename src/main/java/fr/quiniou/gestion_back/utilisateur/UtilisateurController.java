package fr.quiniou.gestion_back.utilisateur;

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
    public ResponseEntity<Page<UtilisateurDTO>> obtenirTousLesUtilisateurs(Pageable pageable) {
        Page<UtilisateurDTO> utilisateurs = utilisateurService.obtenirTousLesUtilisateurs(pageable);
        return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
    }

    // Obtenir un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> obtenirUtilisateurParId(@PathVariable Long id) {
    	UtilisateurDTO utilisateur = utilisateurService.obtenirUtilisateurParId(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        return new ResponseEntity<>(utilisateur, HttpStatus.OK);
    }
    
    @GetMapping("/admin")
    public ResponseEntity<Page<Utilisateur>> obtenirTousLesUtilisateursAdmin(Pageable pageable) {
        Page<Utilisateur> utilisateurs = utilisateurService.obtenirTousLesUtilisateursAdmin(pageable);
        return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
    }

    // Obtenir un utilisateur par son ID
    @GetMapping("/{id}/admin")
    public ResponseEntity<Utilisateur> obtenirUtilisateurParIdAdmin(@PathVariable Long id) {
    	Utilisateur utilisateur = utilisateurService.obtenirUtilisateurParIdAdmin(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur non trouvé"));
        return new ResponseEntity<>(utilisateur, HttpStatus.OK);
    }

    // Ajouter un utilisateur
    @PostMapping
    public ResponseEntity<Utilisateur> ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
    	Utilisateur utilisateurEnregistre = utilisateurService.ajouterUtilisateur(utilisateur);
        return new ResponseEntity<>(utilisateurEnregistre, HttpStatus.CREATED);
    }

    // Mettre à jour un utilisateur
    @PatchMapping("/{id}")
    public ResponseEntity<Utilisateur> mettreAJourUtilisateur(@PathVariable Long id, @RequestBody Utilisateur detailsUtilisateur) {
    	Utilisateur utilisateurMisAJour = utilisateurService.mettreAJourUtilisateur(id, detailsUtilisateur);
        return new ResponseEntity<>(utilisateurMisAJour, HttpStatus.OK);
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
    	utilisateurService.supprimerUtilisateur(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
