package fr.quiniou.gestion_back.frais;

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

import fr.quiniou.gestion_back.frais.dto.FraisDTOForAdminOrProprietaire;

@RestController
@RequestMapping(path = "/api/frais", produces = "application/json")
@CrossOrigin(origins = "${app.cors.origin}")
public class FraisController {
	private final FraisService fraisService;

    public FraisController(FraisService fraisService) {
        this.fraisService = fraisService;
    }
    
    // Obtenir tous les frais avec pagination
    @GetMapping
    public ResponseEntity<Object> obtenirTousLesFrais(Pageable pageable) {
    	try {
    	    Page<Object> frais = fraisService.obtenirTousLesFrais(pageable);
            return new ResponseEntity<>(frais, HttpStatus.OK);
    	}
    	catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    
    }

 // Obtenir un frais par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenirFraisParId(@PathVariable Long id) {
        try {
            Object frais = fraisService.obtenirFraisParId(id);
            return new ResponseEntity<>(frais, HttpStatus.OK);
        } 
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Ajouter un frais
    @PostMapping
    public ResponseEntity<Object> ajouterFrais(@RequestBody Frais frais) {
    	try {
    		FraisDTOForAdminOrProprietaire fraisEnregistre = fraisService.ajouterFrais(frais);
            return new ResponseEntity<>(fraisEnregistre, HttpStatus.CREATED);

    	}
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Mettre à jour un frais
    @PatchMapping("/{id}")
    public ResponseEntity<Object> mettreAJourFrais(@PathVariable Long id, @RequestBody Frais detailsFrais) {
    	try {
    		FraisDTOForAdminOrProprietaire fraisMisAJour = fraisService.mettreAJourFrais(id, detailsFrais);
            return new ResponseEntity<>(fraisMisAJour, HttpStatus.OK);
    	}
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

 // Supprimer un frais
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> supprimerFrais(@PathVariable Long id) {
        try {
        	fraisService.supprimerFrais(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }
    }

    
}
