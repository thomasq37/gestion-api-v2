package fr.quiniou.gestion_back.appartement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import fr.quiniou.gestion_back.appartement.adresse.Adresse;
import fr.quiniou.gestion_back.appartement.details.AppartementDetails;
import fr.quiniou.gestion_back.appartement.statistiques.AppartementStatistiques;

@RestController
@RequestMapping(path = "/api/appartements", produces = "application/json")
@CrossOrigin(origins = "${app.cors.origin}")
public class AppartementController {

    private final AppartementService appartementService;
    
    public AppartementController(AppartementService appartementService) {
        this.appartementService = appartementService;
    }
    
 // Obtenir tous les appartements avec DTO
    @GetMapping
    public ResponseEntity<Page<Object>> obtenirTousLesAppartements(Pageable pageable) {
        Page<Object> appartements = appartementService.obtenirTousLesAppartements(pageable);
        return new ResponseEntity<>(appartements, HttpStatus.OK);
    }

    // Obtenir un appartement par son ID avec DTO
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenirAppartementParId(@PathVariable Long id) {
        try {
        	Object appartement = appartementService.obtenirAppartementParId(id);
            return new ResponseEntity<>(appartement, HttpStatus.OK);
        }
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // Ajouter un appartement avec DTO
    @PostMapping
    public ResponseEntity<Object> ajouterAppartement(@RequestBody Appartement appartement) {
    	try {
    	    Appartement appartementEnregistre = appartementService.ajouterAppartement(appartement);
            return new ResponseEntity<>(appartementEnregistre, HttpStatus.CREATED);
    	}
    	catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    
    }

    // Supprimer un appartement
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> supprimerAppartement(@PathVariable Long id) {
        try {
        	appartementService.supprimerAppartement(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
    
    
    // ------------------------------- Section Adressse Appartement ------------------------------- //
    
    
    // Obtenir adresse appartement par id
    @GetMapping("/{id}/adresse")
    public ResponseEntity<Object> obtenirAdresseAppartementParAppartementId(@PathVariable Long id) {
    	try {
            Adresse adresse = appartementService.obtenirAdresseAppartementParAppartementId(id);
            return ResponseEntity.ok(adresse);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
    
    
	// Mettre a jour adresse appartement par id
    @PutMapping("/{id}/adresse")
    public ResponseEntity<Object> majAdresseAppartementParAppartementId(@PathVariable Long id, @RequestBody Adresse nouvelleAdresse) {
        try {
            Adresse adresse = appartementService.majAdresseAppartementParAppartementId(id, nouvelleAdresse);
            return ResponseEntity.ok(adresse);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
    
    // Supprimer (ou effacer) l'adresse d'un appartement
    @DeleteMapping("/{id}/adresse")
    public ResponseEntity<Object> supprimerAdresseParAppartementId(@PathVariable Long id) {
        try {
            appartementService.supprimerAdresseParAppartementId(id);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }


    // ------------------------------- Section Details Appartement ------------------------------- //

    // Obtenir details appartement par id
    @GetMapping("/{id}/details")
    public ResponseEntity<Object> obtenirDetailsAppartementParAppartementId(@PathVariable Long id) {
    	try {
            AppartementDetails details = appartementService.obtenirDetailsAppartementParAppartementId(id);
            return ResponseEntity.ok(details);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
    
    // Mettre a jour details appartement par id
    @PutMapping("/{id}/details")
    public ResponseEntity<Object> majDetailsAppartementParAppartementId(@PathVariable Long id, @RequestBody AppartementDetails nouveauDetails) {
        try {
            AppartementDetails details = appartementService.majDetailsAppartementParAppartementId(id, nouveauDetails);
            return ResponseEntity.ok(details);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
    
 // Supprimer (ou effacer) les details d'un appartement
    @DeleteMapping("/{id}/details")
    public ResponseEntity<Object> supprimerDetailsParAppartementId(@PathVariable Long id) {
        try {
            appartementService.supprimerDetailsParAppartementId(id);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }

    // ------------------------------- Section Statistiques Appartement ------------------------------- //

    // Obtenir statistiques appartement par id
    @GetMapping("/{id}/statistiques")
    public ResponseEntity<Object> obtenirStatistiquesAppartementParAppartementId(@PathVariable Long id) {
    	try {
            AppartementStatistiques statistiques = appartementService.obtenirStatistiquesParAppartementId(id);
            return ResponseEntity.ok(statistiques);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
    
    // Mettre a jour statistiques appartement par id
    @PutMapping("/{id}/statistiques")
    public ResponseEntity<Object> majStatistiquesAppartementParAppartementId(@PathVariable Long id, @RequestBody AppartementStatistiques nouveauStatistiques) {
        try {
            AppartementStatistiques statistiques = appartementService.majStatistiquesAppartementParAppartementId(id, nouveauStatistiques);
            return ResponseEntity.ok(statistiques);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
    
 // Supprimer (ou effacer) les details d'un appartement
    @DeleteMapping("/{id}/statistiques")
    public ResponseEntity<Object> supprimerStatistiquessParAppartementId(@PathVariable Long id) {
        try {
            appartementService.supprimerStatistiquesParAppartementId(id);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason()); 
        }
    }
    
}
